/* 
 * File:   main.c
 * Author: Tony Mendoza
 *
 * Created on February 27, 2017, 3:36 PM
 */

// All the system resources included.
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>

#define MAX_LINE 80 // Maximum length command
/*
 * A command interpreter that creates processes
 * 
 */
int main(void){
    char *args[MAX_LINE / 2 + 1]; // command line arguments
    char *history[MAX_LINE / 2 + 1]; // history of command lines
    int history_size = 0; // current size of history
    int should_run = 1; // flag to determine when to exit program
    char line[MAX_LINE * 4]; // a char array to store the command.
    int i; // used for iterating
    int should_wait; // flag if the parent process should wait
    int timer = 0;
        
    // Welcome the nice user. UPDATE: Commented out because it interferes with program. Ask professor about it later.
    // printf("Welcome to the UNIX Shell program.\nKeep entering commands at the prompt:\n");
    
    while(should_run) // while the program should run
    {
        printf("osh> "); // The user prompt
        fflush(stdout); // Specify the bufferstream

        // Step 0: Read and process the user's commands
        scanf("%s", line); // store the user input into the line char array.
        char *token = strtok(line, " "); // An array to split the command array, word by word by whitespace.
        i = 0; // set to 0. used to iterate through the args.
        while(token) // Loop on while token has more words and iterate to less than the size of args
        {
            args[i] = token; // store the token in args at the index
            i++; // iterate
            token = strtok(NULL, " "); // remove the word from token
        }
        
        should_wait = 0; // set to 0. flag if the parent process should wait
        if(strcmp(args[i - 1], "&") == 0) // if the user entered a wait param at the end
        {
            //args[i - 1] = NULL; // remove the wait symbol from args
            should_wait = 1; // the parent process should wait
        }
        
        if (strcmp(args[0], "history") == 0) // if the user entered a history command first
        {
            printf("Couldn\'t get history to work properly."); // sorry
            /*if (history_size > 0) {
                if (i > 1) {
                    if (strcmp(args[1], "!") == 0) {
                        if (i > 2) {
                            int n = (int)args[2];
                            if(n >= 0 && n < history_size)
                            {
                                printf("%d %s\n", n - 1, history[n]);
                            }
                            else
                            {
                                printf("No command found at index: %d\n", n);
                            }
                        }
                        else{
                            printf("An integer must be entered after parameter \"!\"\n");
                        }
                    }
                    else if (strcmp(args[1], "!!") == 0){
                        printf("%d %s\n", history_size - 1, history[history_size - 1]);
                    }
                } else {
                    int n;
                    for (n = history_size - 1; n >= 0 && n > history_size - 1 - 10; n--) {
                        printf("%d %s\n", n, history[n]);
                    }
                }
            } else {
                printf("There is no history of commands to print. Enter a command.\n");
            }
            continue;*/
        } else if (strcmp(args[0], "exit") == 0) // else if the user entered an exit command first
        {
            should_run = 0; // the program should stop running
            continue; // skip everything below, exit immediately
        }
        
        // Step 1: Fork a child process using "fork" 
        pid_t pid; // the child process (and its id, I think)
        pid = fork(); // fork the child
        if (pid < 0) // if the child process is not found?
        {
            fprintf(stderr, "Fork Failed"); // forking failed, let the user know
            // return 1; // commenting out, not sure if needed.
        }
        else if (pid == 0)
        {
            //execlp("bin/ls", "ls", NULL); // commenting out, I don't think this is needed.
            // Step 2: Execute the command
            pid = execvp(args[0], args); // execute, args[0] is the command and the rest are params
            
            // Step 3: Wait if asked to
            int status; // wait status
            //if(should_wait) // if should wait
            //    wait(&status); // then wait
        }
        else
        {
            // Step 3: Not sure how this works. Another waiting?
            wait(NULL);
            printf("Child Complete");
        }
        printf("\n");
        // history[history_size] = line;
        // *line; // try to dereference line and put it in history 
        // printf("%s saved", history[history_size]);
        // history_size = history_size + 1;
    }
    //exit(0);
    printf("\nShutting down. Hope you had fun."); // Tell the nice user goodbye.
    return (EXIT_SUCCESS); // exit the program
}