### These implementation of producer consumer problem simulation is taken from Operating Systems Design and Implementaion (3rd Edition)

`fatal_race.c`: In this code the problem arises because there is no mutex guard on the variable `producer_f` and `consumer_f`.

`semaphore_imple.c`: Solve the fatal_race.c problem by using three semaphore full, empty and mutex. full and empty are used to check the buffer situation and mutex is used to guard the buffer access (although our simulation doesn't have a buffer).
