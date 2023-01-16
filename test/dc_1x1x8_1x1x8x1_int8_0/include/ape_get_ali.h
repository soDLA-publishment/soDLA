#ifndef APE_GET_ALI_H 
#define APE_GET_ALI_H 

// #define APE_OPERATION_NUM 40
#define WEIGHT_SIZE 0x8

#define INPUT_SIZE 0x8

#define NULL (void*)0

typedef signed char ScS8 ;
typedef signed short ScS16 ;
typedef signed int ScS32 ;
typedef signed long long ScS64 ;

typedef unsigned char ScU8 ;
typedef unsigned short ScU16 ;
typedef unsigned int ScU32 ;
typedef unsigned long long ScU64 ;
typedef unsigned long  ScSU64 ;

void * ape_get_ali1(void); 
void * ape_get_ali3(void); 
void * ape_get_right_value(void);

#endif 
