
SRC_DIR=src
BUILD_DIR=build
INCLUDE_DIR=include
LIB_DIR=../resources/obp2/cesmi/lib/

CC_FLAGS=-g -O0

all: lib exe libbakery.1.cesmi libbakery.1.prop2.cesmi

lib: ${LIB_DIR} ${BUILD_DIR} | obp2_cesmi_loader.o obp2_cesmi.o obp2_cesmi_jni.o
	gcc ${CC_FLAGS} \
		-fPIC -dynamiclib \
		"${BUILD_DIR}/objects/"obp2_cesmi_loader.o \
		"${BUILD_DIR}/objects/"obp2_cesmi.o \
		"${BUILD_DIR}/objects/"obp2_cesmi_jni.o \
		-o "${LIB_DIR}/"libcesmi.dylib

libbakery.1.cesmi: ${BUILD_DIR} | ${SRC_DIR}/bakery/bakery.1.dve.cpp
	gcc ${CC_FLAGS} \
		-fPIC -dynamiclib \
		-Wno-return-type -Wno-parentheses-equality \
		${SRC_DIR}/bakery/bakery.1.dve.cpp \
		-o "${BUILD_DIR}/"libbakery.1.cesmi

libbakery.1.prop2.cesmi: ${BUILD_DIR} | ${SRC_DIR}/bakery/bakery.1.prop2.dve.cpp
	gcc ${CC_FLAGS} \
		-fPIC -dynamiclib \
		-Wno-return-type -Wno-parentheses-equality \
		${SRC_DIR}/bakery/bakery.1.prop2.dve.cpp \
		-o "${BUILD_DIR}/"libbakery.1.prop2.cesmi

exe: ${BUILD_DIR} | obp2_cesmi_loader.o obp2_cesmi.o obp2_cesmi_main.o
	gcc ${CC_FLAGS} \
		"${BUILD_DIR}/objects/"obp2_cesmi_loader.o \
		"${BUILD_DIR}/objects/"obp2_cesmi.o \
		"${BUILD_DIR}/objects/"obp2_cesmi_main.o \
		-o "${BUILD_DIR}/bin/"obp2_cesmi_main

cesmi_jni.o: ${SRC_DIR}/cesmi_jni.c
	gcc ${CC_FLAGS} -I"${JAVA_HOME}"/include -I"${JAVA_HOME}"/include/darwin -I"${INCLUDE_DIR}" -c ${SRC_DIR}/cesmi_jni.c -o "${BUILD_DIR}/objects/"cesmi_jni.o

main_simple.o: ${SRC_DIR}/main_simple.c
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/main_simple.c -o "${BUILD_DIR}/objects/"main_simple.o

cesmi_next_stream.o: ${SRC_DIR}/cesmi_next_stream.c 
	gcc ${CC_FLAGS} -I"${INCLUDE_DIR}" -c ${SRC_DIR}/cesmi_next_stream.c -o "${BUILD_DIR}/objects/"cesmi_next_stream.o

bakery.1.o: ${SRC_DIR}/bakery/bakery.1.dve.cpp 
	gcc ${CC_FLAGS} -Wno-return-type -Wno-parentheses-equality -c ${SRC_DIR}/bakery/bakery.1.prop2.dve.cpp -o "${BUILD_DIR}"/objects/bakery.1.o

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