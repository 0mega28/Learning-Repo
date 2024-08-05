# LogClient Interface Implementation

This repository contains an implementation of the `LogClient` interface as described in a Google Low-Level Design interview question. The implementation is inspired by the YouTube video [Low Level Design of a Logging System](https://www.youtube.com/watch?v=bPkXQszkkpY&list=PLMCXHnjXnTnvQVh7WsgZ8SurU1O2v_UM7&index=7).

## Overview

The `LogClient` interface provides methods for tracking the start and end times of processes, and for polling the logs of completed processes in a sorted manner.

## Interface Methods

### `void start(String processId, long timestamp)`

- **Description**: Called when a process starts. It records the process ID and its start timestamp.
- **Parameters**:
    - `processId`: A unique identifier for the process.
    - `timestamp`: The start time of the process.

### `void end(String processId)`

- **Description**: Called when the same process ends. It records the end timestamp of the process.
- **Parameters**:
    - `processId`: The unique identifier for the process.

### `String poll()`

- **Description**: Polls the first log entry of a completed process, sorted by the start time of the processes.
- **Returns**: A string in the format `{processId} started at {startTime} and ended at {endTime}`. The process entries are sorted by their start times.

## Example

### Given the following process logs:

- Process ID `1` starts at timestamp `12` and ends at `15`.
- Process ID `2` starts at timestamp `8` and ends at `12`.
- Process ID `3` starts at timestamp `7` and ends at `19`.

### The poll method should return:

1. `{3} started at {7} and ended at {19}`
2. `{2} started at {8} and ended at {12}`
3. `{1} started at {12} and ended at {15}`

## Acknowledgments

This implementation is inspired by the YouTube video [Low Level Design of a Logging System](https://www.youtube.com/watch?v=bPkXQszkkpY&list=PLMCXHnjXnTnvQVh7WsgZ8SurU1O2v_UM7&index=7).