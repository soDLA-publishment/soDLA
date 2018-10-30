#!/usr/bin/env bash
args=$@
sbt "test:runslibs slibs.Launcher $args"
