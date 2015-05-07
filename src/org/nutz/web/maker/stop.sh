#!/bin/bash
(sleep 2;echo "stop"; sleep 1) | telnet 127.0.0.1 ${app-admin-port}