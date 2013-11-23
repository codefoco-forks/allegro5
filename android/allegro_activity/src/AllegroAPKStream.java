package org.liballeg.android;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

public class AllegroAPKStream
{
   AllegroActivity activity;
   String fn;
   InputStream in;
   private long pos;
   long fsize;
   boolean at_eof;

   public AllegroAPKStream(AllegroActivity activity, String filename)
   {
      this.activity = activity;
      fn = Path.simplifyPath(filename);
      if (!fn.equals(filename)) {
         Log.d("APK", filename + " simplified to: " + fn);
      }
      pos = 0;
      fsize = -1;
      at_eof = false;
   }

   private boolean reopen()
   {
      try {
         if (in != null) {
            in.close();
         }
         in = activity.getResources().getAssets().open(fn, AssetManager.ACCESS_RANDOM);
         in.mark((int)Math.pow(2, 31));
         if (in == null)
            return false;
         return true;
      }
      catch (IOException e) {
         Log.d("APK", "Got IOException in reopen. fn='" + fn + "'");
         return false;
      }
   }

   public boolean open()
   {
      try {
         AssetFileDescriptor fd = activity.getResources().getAssets().openFd(fn);
         fsize = fd.getLength();
         fd.close();
      }
      catch (IOException e) {
         Log.w("APK", "could not get file size: " + e.toString());
         fsize = -1;
      }

      boolean res = reopen();
      if (!res)
         return false;

      return true;
   }

   public void close()
   {
      try {
         in.close();
      }
      catch (IOException e) {
         Log.d("APK", "IOException in close");
      }
   }

   private void force_skip(long n)
   {
      if (n <= 0)
         return;

      try {
         /* NOTE: in.skip doesn't work here! */
         byte[] b = new byte[(int)n];
         while (n > 0) {
            long res = in.read(b, 0, (int)n);
            if (res <= 0)
               break;
            pos += res;
            n -= res;
         }
      }
      catch (IOException e) {
      }
   }

   public boolean seek(long seekto)
   {
      at_eof = false;

      if (seekto >= pos) {
         long seek_ahead = seekto - pos;
         force_skip(seek_ahead);
      }
      else {
         try {
            in.reset();
         }
         catch (IOException e) {
            reopen();
         }
         pos = 0;
         force_skip(seekto);
      }
      return true;
   }

   public long tell()
   {
      return pos;
   }

   public int read(byte[] b)
   {
      try {
         int ret = in.read(b);
         if (ret > 0)
            pos += ret;
         else if (ret == -1) {
            at_eof = true;
         }
         return ret;
      }
      catch (IOException e) {
         Log.d("APK", "IOException in read");
         return -1;
      }
   }

   public long size()
   {
      return fsize;
   }

   public boolean eof()
   {
      return at_eof;
   }
}

/* vim: set sts=3 sw=3 et: */
