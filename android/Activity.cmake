set(ACTIVITY_DIR ${CMAKE_CURRENT_SOURCE_DIR}/allegro_activity)
set(ACTIVITY_JAR ${LIBRARY_OUTPUT_PATH}/Allegro5.jar)

set(ACTIVITY_SOURCES
    ${ACTIVITY_DIR}/AndroidManifest.xml
    ${ACTIVITY_DIR}/build.xml
    ${ACTIVITY_DIR}/local.properties
    ${ACTIVITY_DIR}/project.properties
    ${ACTIVITY_DIR}/src/org/liballeg/app/AllegroAPKStream.java
    ${ACTIVITY_DIR}/src/org/liballeg/app/AllegroActivity.java
    ${ACTIVITY_DIR}/src/org/liballeg/app/AllegroInputStream.java
    )

add_custom_command(
    OUTPUT ${ACTIVITY_DIR}/local.properties
    COMMAND ${ANDROID_TOOL} update project -p ${ACTIVITY_DIR}
    )

configure_file(
    ${ACTIVITY_DIR}/localgen.properties.cmake
    ${ACTIVITY_DIR}/localgen.properties
    )

add_custom_command(
    OUTPUT ${ACTIVITY_JAR}
    DEPENDS ${ACTIVITY_SOURCES}
    WORKING_DIRECTORY ${ACTIVITY_DIR}
    COMMAND ${ANT_COMMAND} debug jar
    VERBATIM
    )

add_custom_target(jar
    ALL
    DEPENDS ${ACTIVITY_JAR}
    )

# Make this available for building examples in other directories.
set(ACTIVITY_JAR ${ACTIVITY_JAR} PARENT_SCOPE)

#-----------------------------------------------------------------------------#
# vim: set sts=4 sw=4 et:
