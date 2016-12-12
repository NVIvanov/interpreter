grammar Robot;
@header {
    package generated;
}
main: (sentense | functionDeclaration)*;
sentense: whileCycle
    | ifExpr
    | variableDeclaration SEMI NEWLINE*
    | constVariableDeclaration SEMI NEWLINE*
    | arrayDeclaration SEMI NEWLINE*
    | doubleArrayDeclaration SEMI NEWLINE*
    | arrayExtend SEMI NEWLINE*
    | doubleArrayExtend SEMI NEWLINE*
    | arraySize SEMI NEWLINE*
    | doubleArraySize SEMI NEWLINE*
    | assignment SEMI NEWLINE*
    | functionCall SEMI NEWLINE*
    | NEWLINE* expr SEMI NEWLINE*
    | NEWLINE* '{' NEWLINE* (sentense)+ NEWLINE* '}' NEWLINE*;
expr: increment
    | decrement
    | 'NOT' expr
    | '(' expr ')'
    | expr op=('*'|'/') expr
    | expr op=('+'|'-') expr
    | expr op=('GT'|'LT') expr
    | expr 'AND' expr
    | expr 'OR' expr
    | expr '==' expr
    | value;
value: literal
    | ID
    | arrayValue
    | doubleArrayValue;
assignment: ID '=' expr;
whileCycle: 'while' '('expr')' sentense;
ifExpr: 'if' '(' expr ')' sentense 'else' sentense
    | 'if' '(' expr ')' sentense;
variableDeclaration: ('int' | 'bool') ID '=' expr;
constVariableDeclaration: ('cint' | 'cbool') ID '=' expr;
functionDeclaration: returnValues 'function' ID '(' arguments '){' NEWLINE* sentense* '}' NEWLINE*;
functionCall: ID '=' ID '(' (expr?(','expr?)*)? ')'
    | '[' ID(','ID?)*']' '=' ID '(' (expr?(','expr?)*)? ')';
arrayValue: ID '[' expr ']';
arrayDeclaration: ('intarray'|'boolarray') ID '=' arrayInit;
arrayInit: '[' (expr (',' expr)*)? ']';
doubleArrayValue: ID '[' expr ',' expr ']';
doubleArrayDeclaration: ('int2array'|'bool2array') ID '=' doubleArrayInit;
doubleArrayInit: '[' ('[' expr (',' expr)* ']' (',' '[' expr (',' expr)* ']' )*)? ']';
arrayExtend: 'EXTEND1' ID INT;
doubleArrayExtend: 'EXTEND2' ID INT INT;
arraySize: 'SIZE1' ID;
doubleArraySize: 'SIZE2' ID INT;
increment: 'INC' increment
    | 'INC' decrement
    | 'INC' ID;
decrement: 'DEC' decrement
    | 'DEC' increment
    | 'DEC' ID;
returnValues: '[' (returnValue(','returnValue)*)+ ']' 
    | returnValue;
returnValue: ID '=' literal;
arguments: ( argument (',' argument )*)?;
argument: returnValue; //cause same regex
literal: BOOL | INT;
BOOL: 'true' | 'false';
ID: [a-zA-Z][A-Za-z0-9]* ;
INT: [-]?[0-9]+;
NEWLINE: [\r\n|\n|\r] {skip();};
SEMI: ';';
SPACE: (' ')+ {skip();};