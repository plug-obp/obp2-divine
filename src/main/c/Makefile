
SRC_DIR=src
BUILD_DIR=build
INCLUDE_DIR=include
LIB_DIR=../resources/obp2/divine/lib/

CC_FLAGS=-g -O0

all: lib exe

lib: ${LIB_DIR} ${BUILD_DIR} | bakery.1.o cesmi_next_stream.o cesmi_jni.o
	gcc ${CC_FLAGS} \
		-fPIC -dynamiclib \
		"${BUILD_DIR}/objects/"bakery.1.o \
		"${BUILD_DIR}/objects/"cesmi_next_stream.o \
		"${BUILD_DIR}/objects/"cesmi_jni.o \
		-o "${LIB_DIR}/"libcesmi.dylib

exe: ${BUILD_DIR} | bakery.1.o cesmi_next_stream.o main_simple.o
	gcc ${CC_FLAGS} \
		"${BUILD_DIR}/objects/"bakery.1.o \
		"${BUILD_DIR}/objects/"cesmi_next_stream.o \
		"${BUILD_DIR}/objects/"main_simple.o \
		-o "${BUILD_DIR}/bin/"main_simple

cesmi_jni.o: ${SRC_DIR}/cesmi_jni.c
	gcc ${CC_FLAGS} -I"${JAVA_HOME}"/include -I"${JAVA_HOME}"/include/darwin -I"${INCLUDE_DIR}" -c ${SRC_DIR}/cesmi_jni.c -o "${BUILD_DIR}/objects/"cesmi_jni.o

main_simple.o: ${SRC_DIR}/main_simple.c
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/main_simple.c -o "${BUILD_DIR}/objects/"main_simple.o

cesmi_next_stream.o: ${SRC_DIR}/cesmi_next_stream.c 
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/cesmi_next_stream.c -o "${BUILD_DIR}/objects/"cesmi_next_stream.o

bakery.1.o: ${SRC_DIR}/bakery/bakery.1.dve.cpp 
	gcc ${CC_FLAGS} -Wno-return-type -Wno-parentheses-equality -c ${SRC_DIR}/bakery/bakery.1.prop3.dve.cpp -o "${BUILD_DIR}"/objects/bakery.1.o

${BUILD_DIR}:
	mkdir -p ${BUILD_DIR} ${BUILD_DIR}/objects ${BUILD_DIR}/bin

${LIB_DIR}:
	mkdir -p ${LIB_DIR}

clean:
	rm -rf ${BUILD_DIR}