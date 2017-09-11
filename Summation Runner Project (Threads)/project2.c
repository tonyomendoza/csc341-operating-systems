/* 
 * File: project2
 * Author: Tony Mendoza
 *
 * Created on March 23, 2017, X:YZ PM
 */

// All the system resources included.
#include <pthread.h>
#include <time.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define NUM_ARRAY 20000 /* number of elements (numbers) for the array */
#define NUM_THREAD 10 /* number of threads for the program */

int numbers[NUM_ARRAY]; /* array is shared by all threads */
int sum; /* stores the sum of all the elements in the array  */
void *runner(void *param); /* threads call this function */
/* this struct stores variables that will be passed into the runner */
struct runner_struct{
    int *array; /* pointer to the numbers array */
    int id; /* for printing purposes, irrelevant to computation */
    int min; /* stores the minimum index for a thread to traverse */
    int max; /* stores the the maximum index for a thread to traverse */
    int sum; /* stores the thread's component of the array's total sum */
};

/*
 * 
 */
int main(void) {
    clock_t t; /* used to measure the execution time */
    t = clock(); /* mark the current time */
    
    printf("Creating the array... ");
    int i; /* used for iteration */
    for (i = 0; i < NUM_ARRAY; i++){ /* iterate through all the elements */
        numbers[i] = rand() % 100; /* Set a random number between 0 and 100*/
        if (rand() % 2 == 0){ /* randomly determine if the number should be negative */
            numbers[i] *= -1;
        }
    }
    printf("Array created.\n");
    
    sum = 0; /* set sum to 0 before adding everything together. */
    printf("This is a single thread. Time is clocked now.\n");
    
    struct runner_struct params[NUM_THREAD]; /* the data to pass through the thread */
    /* Iterate to create the data to pass through the threads. */
    for(i = 0; i < NUM_THREAD; i++){
        params[i].id = i; /* when the thread was created at the iteration */
        params[i].array = &numbers; /* point to the array */
        params[i].min = (NUM_ARRAY / NUM_THREAD) * i; /* relative start to the thread created */
        params[i].max = (NUM_ARRAY / NUM_THREAD) * (i + 1); /* relative end to the thread created */
        params[i].sum = 0; /* intialize to 0 */
    }
    
    pthread_t tids[NUM_THREAD]; /* the thread identifier */
    pthread_attr_t attr; /* set of thread attributes */
    /* get the default attributes */
    pthread_attr_init(&attr);
    /* iterate to create the threads one by one, then join them. */
    for(i = 0; i < NUM_THREAD; i++){
        /* create the thread */
        pthread_create(&tids[i], &attr, runner, &params[i]);
        /* wait for the thread to exit? */
        pthread_join(tids[i], NULL);
    }    
    
    /* wait for the thread to exit */
    /* I first assumed join should be outside the creation loop. It is actually slower. (but how?) */
    /*for(i = 0; i < NUM_THREAD; i++){
        pthread_join(tids[i], NULL);
    }*/
    
    /* iterate to combine each of the thread's calculated sum. The total sum. */
    for(i = 0; i < NUM_THREAD; i++){
        sum += params[i].sum;
    }
    
    t = clock() - t; /* mark the current time minus the last time */
    
    /* Print the sum and the time. */
    printf("sum = %d, run time = %f milliseconds.\n", sum, (((double)t)/CLOCKS_PER_SEC * 1000));
    printf("Have a nice day!\n");
    
    return 0;
}

/*
 * This is the function the thread will be running. Hence, runner. Right?
 */
void *runner(void *arg){
    int i, sum; /* used for iteration */
    struct runner_struct *arg_struct = (struct runner_struct*) arg; /* get the struct */
    int* array = arg_struct->array; /* get the array */
    
    /* iterate through the range of elements */
    for (i = arg_struct->min; i < arg_struct->max; i++){
        sum = arg_struct->sum + array[i]; /* get the sum first for printing purposes */
        printf("Thread %d, Element %d: %d + %d = %d\n",
                arg_struct->id, i + 1, arg_struct->sum, array[i], sum); /* print what the thread is doing. */
        arg_struct->sum = sum; /* update the sum. */
    }
    
    pthread_exit(0); /* exit */
}