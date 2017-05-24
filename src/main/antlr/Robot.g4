grammar Robot;
@header {
    package generated;
}
main: (sentense | functionDeclaration)*;
sentense: comment
    | breakpoint
    | '{' (sentense)+ '}'
    | whileCycle
    | ifExpr
    | variableDeclaration SEMI
    | constVariableDeclaration SEMI
    | arrayDeclaration SEMI
    | doubleArrayDeclaration SEMI
    | arrayExtend SEMI
    | doubleArrayExtend SEMI
    | assignment SEMI
    | functionCall SEMI
    | expr SEMI;
comment: '/*' sentense* '*/';
breakpoint: 'bp';
expr: increment #IncrementLabel
    | decrement #DecrementLabel
    | arraySize #arraySizeLabel
    | doubleArraySize #doubleArraySizeLabel
    | oneReturnValueFunctionCall #OneReturnFunction
    | 'NOT' expr #Negation
    | '(' expr ')' #Priotiry
    | expr op=('*'|'/') expr #Multiplying
    | expr op=('+'|'-') expr #Addition
    | expr op=('GT'|'LT') expr #Comparation
    | expr '==' expr #Equal
    | expr 'AND' expr #AND
    | expr 'OR' expr #OR
    | value #ValueLabel;
value: literal
    | identifier
    | arrayValue
    | doubleArrayValue;
assignment: identifier '=' expr
    | arrayValue '=' expr
    | doubleArrayValue '=' expr;
whileCycle: 'while' '('expr ')' sentense;
ifExpr: 'if' '(' expr ')' sentense 'else' sentense
    | 'if' '(' expr ')' sentense;
variableDeclaration: ('int' | 'bool') identifier '=' expr;
constVariableDeclaration: ('cint' | 'cbool') identifier  '=' expr;
functionDeclaration: returnValues 'function' identifier  '(' arguments ')''{'  sentense* '}';
functionCall: identifier '(' (expr?(','expr?)*)? ')' #Procedure
    | '[' identifier (','identifier ?)*']' '=' identifier  '(' (expr?(','expr?)*)? ')' #SeveralReturnFunction;
oneReturnValueFunctionCall: identifier '(' (expr?(','expr?)*)? ')';
arrayValue: identifier  '[' expr ']';
arrayDeclaration: ('intarray'|'boolarray') identifier '=' arrayInit;
arrayInit: '[' (expr (',' expr)*)? ']';
doubleArrayValue: identifier  '[' expr ',' expr ']';
doubleArrayDeclaration: ('int2array'|'bool2array') identifier  '=' doubleArrayInit;
doubleArrayInit: '[' ( arrayInit (',' arrayInit )*)? ']';
arrayExtend: 'EXTEND1' identifier expr;
doubleArrayExtend: 'EXTEND2' identifier expr expr;
arraySize: 'SIZE1' identifier ;
doubleArraySize: 'SIZE2' identifier expr;
increment: 'INC' identifier
    | 'INC' arrayValue
    | 'INC' doubleArrayValue;
decrement: 'DEC' identifier
    | 'DEC' arrayValue
    | 'DEC' doubleArrayValue;
returnValues: ('[' (returnValue(','returnValue)*)+ ']')?
    | returnValue;
returnValue: identifier '=' literal
    | identifier '=' arrayLiteral;
arguments: ( argument (',' argument )*)?;
argument: returnValue; //cause same regex
literal: BOOL | INT;
arrayLiteral: '[' (literal (',' literal)*)? ']';
BOOL: 'true' | 'false';
identifier: ID;
ID: [a-zA-Z][A-Za-z0-9]* ;
INT: [0-9]+;
NEWLINE: [\r\n] {skip();};
SEMI: ';';
SPACE: (' ')+ {skip();};