grammar SConfig;

@header {
package com.slava_110.sconfig;
}

config: (object | list) EOF;

expression: function | functionCall | variable | placeholder | atom | object | list | pair | range;

// Special
function: LBRACKET FUNC STRINGLIKE functionParams expression RBRACKET;

functionParams: LBRACKET STRINGLIKE* RBRACKET;

functionCall: LBRACKET FUNC_CALL STRINGLIKE LBRACKET expression* RBRACKET RBRACKET;

variable: LBRACKET VAR STRINGLIKE expression RBRACKET;

placeholder: PLACEHOLDER_PREFIX STRINGLIKE;

range: LBRACKETSQ NUMBER DOUBLE_DOT RBRACKETSQ;

// Complex
object: LBRACKET keyPair* RBRACKET;

list: LBRACKET expression* RBRACKET;

keyPair: LBRACKET STRINGLIKE DOT expression RBRACKET;

pair: LBRACKET expression DOT expression RBRACKET;

// Primitive
atom: STRINGLIKE | NUMBER;

//WS : [ \r\t\n]+ -> skip;
WS: [ \r\t\n]+ -> channel(HIDDEN);

VAR: 'var';
FUNC: 'func';
FUNC_CALL: '@';

STRINGLIKE: STRING | SYMBOL;

NUMBER: '-'? DIGIT+ ('.' DIGIT+)?;

STRING: '"' ( '\\' [btnfr"'\\] | ~[\r\n\\"] )* '"';

SYMBOL: LETTER ( LETTER | DIGIT )*;

DOT: '.';
LBRACKET: '(';
RBRACKET: ')';

LBRACKETSQ: '[';
RBRACKETSQ: ']';
DOUBLE_DOT: '..';

PLACEHOLDER_PREFIX: '$';

fragment LETTER: [a-zA-Z];

fragment DIGIT: [0-9];