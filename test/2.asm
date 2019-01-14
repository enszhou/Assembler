.ORIG	xC000
;.Origin randomly from x3000 to xC000
LD R6 StackBehindBase ;Create the Stack
ADD R5 R6 #0
JSR main

LEA R0 STR_Finished
PUTS
LDR R0 R6 #0
ADD R6 R6 #1
HALT
;

; main()
;save R7 R5 and set R6 R5
main ADD R6 R6 #-2 ;Push return value
STR R7 R6 #0
ADD R6 R6 #-1
STR R5 R6 #0
ADD R5 R6 #-1   
ADD R6 R6 #-2   ;the first is n,the second is value of func return 
;save R7 R5 and set R6 R5

;the body of the main()
TRAP	x20
OUT
LD R1 ASCIIoffset
ADD R2 R0 R1
STR R2 R5 #0

;call FUNC
;set arguments
AND R1 R1 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R1 R6 #0
ADD R6 R6 #-1
STR R2 R6 #0
;set arguments
JSR FUNC

LDR R0 R6 #0 ;load return value
STR R0 R5 #-1;set return value 
ADD R6 R6 #8 ;pop return value and arguments
;call FUNC

;return
LDR R0 R5 #-1
STR R0 R5 #3
ADD R6 R5 #1
LDR R5 R6 #0
ADD R6 R6 #1
LDR R7 R6 #0
ADD R6 R6 #1
RET
;main()


;FUNC
;save R7 R5 and set R6 R5
FUNC ADD R6 R6 #-2 ;Push return value
STR R7 R6 #0    ;save R7
ADD R6 R6 #-1
STR R5 R6 #0    ;Save R5
ADD R5 R6 #-1   ;set R5
ADD R6 R6 #-3   ;the first is t,the second is x,the third is y
;save R7 R5 and set R6 R5


;save R0 R1
ADD R6 R6 #-1
STR R0 R6 #0 
ADD R6 R6 #-1
STR R1 R6 #0 
ADD R6 R6 #-1
STR R2 R6 #0 
ADD R6 R6 #-1
STR R3 R6 #0 

;check the stack is overflow or not
LD R7 StackMaxTop
ADD R7 R7 R6
BRnz	StackOverflow
;

;the body of FUNC()
TRAP x20
OUT
LD R1 ASCIIoffset
ADD R0 R1 R0

LDR R1 R5 #5 ;a
ADD R0 R1 R0
LDR R1 R5 #6
ADD R0 R1 R0
LDR R1 R5 #7
ADD R0 R1 R0
LDR R1 R5 #8
ADD R0 R1 R0
LDR R1 R5 #9
ADD R0 R1 R0
LDR R1 R5 #10 ;f
ADD R0 R1 R0

STR R0 R5 #0 ;t

LDR R1 R5 #4 ;n
ADD R2 R1 #-1 ;n-1
BRnz ELSE

;if true

;call FUNC_x
;set arguments
LDR R1 R5 #10 ;f
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #9 ;e
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #8 ;d
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #7 ;c
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #6 ;b
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #5 ;a
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #4 ;n
ADD R2 R1 #-1 ;n-1
ADD R6 R6 #-1
STR R2 R6 #0
;set arguments
JSR FUNC

LDR R0 R6 #0 ;load return value
STR R0 R5 #-1;set return value to x
ADD R6 R6 #8 ;pop return value and arguments
;call FUNC_x

;call FUNC_y
;set arguments
LDR R1 R5 #10 ;f
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #9 ;e
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #8 ;d
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #7 ;c
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #6 ;b
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #5 ;a
ADD R6 R6 #-1
STR R1 R6 #0
LDR R1 R5 #4 ;n
ADD R2 R1 #-2 ;n-2
ADD R6 R6 #-1
STR R2 R6 #0
;set arguments
JSR FUNC

LDR R0 R6 #0 ;load return value
STR R0 R5 #-2;set return value to y
ADD R6 R6 #8 ;pop return value and arguments
;call FUNC_y

;return
LDR R0 R5 #0 ;Load t
ADD R0 R0 #-1 ;t-1
LDR R1 R5 #-1 ;x
ADD R0 R1 R0;
LDR R1 R5 #-2 ;y
ADD R0 R1 R0 ;x+y+t-1
STR R0 R5 #3 ;set return value

;recover R0,R1,R2,R3 R5 R7
LDR R3 R6 #0
ADD R6 R6 #1
LDR R2 R6 #0
ADD R6 R6 #1
LDR R1 R6 #0
ADD R6 R6 #1
LDR R0 R6 #0
ADD R6 R6 #1
;
ADD R6 R5 #1
LDR R5 R6 #0 ;recover R5
ADD R6 R6 #1
LDR R7 R6 #0 ;recover R7
ADD R6 R6 #1
RET
;if


;else
;return
ELSE  LDR R0 R5 #0 ;Load return value
STR R0 R5 #3 ;set return value

;recover R0,R1,R2,R3
LDR R3 R6 #0
ADD R6 R6 #1
LDR R2 R6 #0
ADD R6 R6 #1
LDR R1 R6 #0
ADD R6 R6 #1
LDR R0 R6 #0
ADD R6 R6 #1
;
ADD R6 R5 #1
LDR R5 R6 #0 ;recover R5
ADD R6 R6 #1
LDR R7 R6 #0 ;recover R7
ADD R6 R6 #1
RET
;else
;FUNC()

StackOverflow LEA R0 STR_StackOverflow
PUTS
HALT

ASCIIoffset .FILL	#-48
StackBehindBase .FILL	xF000
StackMaxTop .FILL	x3000   ; the stack starts from xD000 to xEFFF,x3000 = -xD000
STR_Finished .FILL x000A
.STRINGZ	"Finished!"
STR_StackOverflow .FILL x000A
.STRINGZ	"StackOverflow!"
.END