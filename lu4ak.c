#include <sys/types.h>
#include <dirent.h>
#include <stdio.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>   

void check_params(int argc, char** argv);
void get_input(char* dir_path);
char* generate_path(char* name);

int params[3] = {0};

int main(int argc, char** argv) {
	check_params(argc, argv);
	if (argc == 1) {
		get_input(".");
	} else {
		for (int i = 1; i < argc; i++) {
			get_input(argv[i]);
		}
	}
	return 0;
}

void check_params(int argc, char** argv) {	
	int opt = 0;
	char *optarg = "AlR";
	while ((opt = getopt(argc, argv, optarg)) != -1) {
		switch (opt) {
			case 'A':
			 	params[0] = 1; break;
			case 'l':
			 	params[1] = 1; break;
			case 'R':
			 	params[2] = 1; break;
			default:
            	perror("Unknown arg");      
				return;
		}
	}
}

void get_input(char* dir_path) {
	DIR* dir = opendir(dir_path);
	struct dirent* entry;
	char buf[PATH_MAX + 1]; 
	while ((entry = readdir(dir)) != NULL) {
	
		realpath(entry->d_name, buf);
		
		struct stat data;
		int result = stat(buf, &data);
		if (entry->d_name[0] == '.') {
			continue;
		}
		
		switch (data.st_mode & S_IFMT) {
			case S_IFBLK:
				printf("b "); printf("%s\n", entry->d_name); break;
			case S_IFCHR:
				printf("c "); printf("%s\n", entry->d_name); break;
			case S_IFDIR:
				printf("d ");
				printf("%s\n", entry->d_name);
				char* path = generate_path(buf);
			 	get_input(path);
				free(path);
				break;
			case S_IFIFO:
				printf("p "); printf("%s\n", entry->d_name); break;
			case S_IFLNK:
				printf("| "); printf("%s\n", entry->d_name); break;
			case S_IFREG:
				printf("- "); printf("%s\n", entry->d_name); break;
			case S_IFSOCK:
				printf("s "); printf("%s\n", entry->d_name); break;
			default:
				perror("ls"); continue;			
		
		}
	}
}

char* generate_path(char* name) {
	char* path = malloc(sizeof(char) * (strlen(name) + 2));
	strcat(path, name); 
	strcat(path, "/");
	return path;
}
