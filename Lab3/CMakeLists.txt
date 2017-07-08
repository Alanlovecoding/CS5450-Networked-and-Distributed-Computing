cmake_minimum_required(VERSION 3.6)
project(p2p)

set(CMAKE_CXX_STANDARD 11)

find_package(Qt5Core REQUIRED)
find_package(Qt5Widgets REQUIRED)
find_package(Qt5Gui REQUIRED)
find_package(Qt5Network REQUIRED)

set(SOURCE_FILES
    cmake-build-debug/moc_main.cpp
    cmake-build-debug/p2p_automoc.cpp
    main.cc
    main.hh)



add_executable(p2p ${SOURCE_FILES})
target_link_libraries(p2p Qt5::Gui Qt5::Widgets Qt5::Core Qt5::Network)