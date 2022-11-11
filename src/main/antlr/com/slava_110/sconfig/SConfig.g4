grammar SConfig;

@header {
package com.slava_110.sconfig;
}

config: list EOF;

object: LBRACKET keyPair* RBRACKET;

list: LBRACKET expression* RBRACKET;

keyPair: LBRACKET STRINGLIKE DOT expression RBRACKET;

pair: LBRACKET expression DOT expression RBRACKET;

//range: LBRACKETSQ NUMBER DOUBLE_DOT RBRACKETSQ;

expression: atom | object | list | pair; //| range

atom: STRINGLIKE | NUMBER;

WS : [ \r\t\n]+ -> skip;

STRINGLIKE: STRING | SYMBOL;

NUMBER: '-'? DIGIT+ ('.' DIGIT+)?;

STRING: '"' ( '\\' [btnfr"'\\] | ~[\r\n\\"] )* '"';

SYMBOL: LETTER ( LETTER | DIGIT )*;

DOT: '.';
LBRACKET: '(';
RBRACKET: ')';

//LBRACKETSQ: '[';
//RBRACKETSQ: ']';
//DOUBLE_DOT: '..';

fragment LETTER: [a-zA-Z];

fragment DIGIT: [0-9];

fragment ESC : '\\"' | '\\\\';