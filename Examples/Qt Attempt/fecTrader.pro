#-------------------------------------------------
#
# Project created by QtCreator 2014-03-21T00:56:13
#
#-------------------------------------------------

QT       += core gui
QT       += network
QT       += testlib
QT       += sql

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = fecTrader
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    client.cpp \
    connection.cpp \
    exchange.cpp \
    server.cpp \
    serverthread.cpp \
    clientmanager.cpp \
    Tests/exchange_tests.cpp \
    databasemanager.cpp

HEADERS  += mainwindow.h \
    client.h \
    connection.h \
    exchange.h \
    server.h \
    serverthread.h \
    clientmanager.h \
    Tests/exchange_test.h \
    databasemanager.h

FORMS    += mainwindow.ui

OTHER_FILES += \
    fecTrader.pro.user

CONFIG += c++11
CONFIG += console
