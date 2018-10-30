#!/usr/bin/env bash
args=$@
sbt "test:runNVDLA nvdla.Launcher $args"