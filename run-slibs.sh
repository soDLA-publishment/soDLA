#!/usr/bin/env bash
args=$@
sbt "test:runMain slibs.Launcher $args"