#include "log.h"
#include <stdio.h>

FILE *logOpen(const char *path)
{
    FILE *logfile;

    logfile = fopen(path, "w");
    if ( logfile == NULL )
    {
        fprintf(stdout, "Error opening logfile. \n");
        exit(EXIT_FAILURE);
    }

    setvbuf(logfile, NULL, _IOLBF, 0);

    return logfile;
}

void Log(const char *s)
{
    time_t ltime;
    struct tm *Tm;

    ltime = time(NULL);
    Tm = localtime(&ltime);

    fprintf(params.log, "[%04d%02d%02d %02d:%02d:%02d] ",
                 Tm->tm_year+1900,
                 Tm->tm_mon+1,
                 Tm->tm_mday,
                 Tm->tm_hour,
                 Tm->tm_min,
                 Tm->tm_sec
           );
    fprintf(params.log, " %s\n", s);
}
