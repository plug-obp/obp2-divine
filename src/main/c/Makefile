
SRC_DIR=src
BUILD_DIR=build
INCLUDE_DIR=include
LIB_DIR=../resources/obp2/cesmi/lib

CC_FLAGS=-g -O0

all: lib exe

lib: ${LIB_DIR} ${BUILD_DIR} | obp2_cesmi_loader.o obp2_cesmi.o obp2_cesmi_jni.o
	gcc ${CC_FLAGS} \
		-fPIC -dynamiclib \
		"${BUILD_DIR}/objects/"obp2_cesmi_loader.o \
		"${BUILD_DIR}/objects/"obp2_cesmi.o \
		"${BUILD_DIR}/objects/"obp2_cesmi_jni.o \
		-o "${LIB_DIR}/"libcesmi.dylib

exe: ${BUILD_DIR} | obp2_cesmi_loader.o obp2_cesmi.o obp2_cesmi_main.o
	gcc ${CC_FLAGS} \
		"${BUILD_DIR}/objects/"obp2_cesmi_loader.o \
		"${BUILD_DIR}/objects/"obp2_cesmi.o \
		"${BUILD_DIR}/objects/"obp2_cesmi_main.o \
		-o "${BUILD_DIR}/bin/"obp2_cesmi_main

obp2_cesmi_loader.o: ${SRC_DIR}/obp2_cesmi_loader.c
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/obp2_cesmi_loader.c -o "${BUILD_DIR}/objects/"obp2_cesmi_loader.o

obp2_cesmi.o: ${SRC_DIR}/obp2_cesmi.c
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/obp2_cesmi.c -o "${BUILD_DIR}/objects/"obp2_cesmi.o

obp2_cesmi_main.o: ${SRC_DIR}/obp2_cesmi_main.c
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/obp2_cesmi_main.c -o "${BUILD_DIR}/objects/"obp2_cesmi_main.o

obp2_cesmi_jni.o: ${SRC_DIR}/obp2_cesmi_jni.c
	gcc ${CC_FLAGS} -I"${JAVA_HOME}"/include -I"${JAVA_HOME}"/include/darwin -I"${INCLUDE_DIR}" -c ${SRC_DIR}/obp2_cesmi_jni.c -o "${BUILD_DIR}/objects/"obp2_cesmi_jni.o

${BUILD_DIR}:
	mkdir -p ${BUILD_DIR} ${BUILD_DIR}/objects ${BUILD_DIR}/bin

${LIB_DIR}:
	mkdir -p ${LIB_DIR}

clean:
	rm -rf ${BUILD_DIR}