#!/usr/bin/env bash
args=$@
sbt "test:runMain examples.Launcher $args"