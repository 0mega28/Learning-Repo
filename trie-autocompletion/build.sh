#!/usr/bin/env bash
set -xe

#####################
clear
#####################

gcc -Wextra -Wall -o trieautocomp trie-autocompletion.c wordlist.h
./trieautocomp zo