grammar Robot;
@header {
    package generated;
}
main: (sentense | functionDeclaration)*;
sentense: whileCycle
    | ifExpr
    | variableDeclaration SEMI
    | constVariableDeclaration SEMI
    | arrayDeclaration SEMI
    | doubleArrayDeclaration SEMI
    | arrayExtend SEMI
    | doubleArrayExtend SEMI
    | arraySize SEMI
    | doubleArraySize SEMI
    | assignment SEMI
    | functionCall SEMI
    | '{' (sentense)+ '}'
    | expr SEMI;
expr: increment #IncrementLabel
    | decrement #DecrementLabel
    | 'NOT' expr #Negation
    | '(' expr ')' #Priotiry
    | expr op=('*'|'/') expr #Multiplying
    | expr op=('+'|'-') expr #Addition
    | expr op=('GT'|'LT') expr #Comparation
    | expr 'AND' expr #AND
    | expr 'OR' expr #OR
    | expr '==' expr #Equal
    | value #ValueLabel;
value: literal
    | identifier 
    | arrayValue
    | doubleArrayValue;
assignment: identifier  '=' expr;
whileCycle: 'while' '('expr ')' sentense;
ifExpr: 'if' '(' expr ')' sentense 'else' sentense
    | 'if' '(' expr ')' sentense;
variableDeclaration: ('int' | 'bool') identifier  '=' expr;
constVariableDeclaration: ('cint' | 'cbool') identifier  '=' expr;
functionDeclaration: returnValues 'function' identifier  '(' arguments ')''{'  sentense* '}';
functionCall: identifier  '=' identifier  '(' (expr?(','expr?)*)? ')'
    | '[' identifier (','identifier ?)*']' '=' identifier  '(' (expr?(','expr?)*)? ')';
arrayValue: identifier  '[' expr ']';
arrayDeclaration: ('intarray'|'boolarray') identifier  '=' arrayInit;
arrayInit: '[' (expr (',' expr)*)? ']';
doubleArrayValue: identifier  '[' expr ',' expr ']';
doubleArrayDeclaration: ('int2array'|'bool2array') identifier  '=' doubleArrayInit;
doubleArrayInit: '[' ('[' expr (',' expr)* ']' (',' '[' expr (',' expr)* ']' )*)? ']';
arrayExtend: 'EXTEND1' identifier  INT;
doubleArrayExtend: 'EXTEND2' identifier  INT INT;
arraySize: 'SIZE1' identifier ;
doubleArraySize: 'SIZE2' identifier  INT;
increment: 'INC' identifier ;
decrement: 'DEC' identifier ;
returnValues: '[' (returnValue(','returnValue)*)+ ']'
    | returnValue;
returnValue: identifier  '=' literal;
arguments: ( argument (',' argument )*)?;
argument: returnValue; //cause same regex
literal: BOOL | INT;
BOOL: 'true' | 'false';
identifier: ID;
ID: [a-zA-Z][A-Za-z0-9]* ;
INT: [0-9]+;
NEWLINE: [\r\n] {skip();};
SEMI: ';';
SPACE: (' ')+ {skip();};