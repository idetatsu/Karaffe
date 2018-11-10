grammar Karaffe;

compilationUnit
  : classDef? EOF
  ;

classDef
  : CLASS Identifier
  ;

CLASS: 'class';

Identifier
  : Letter LetterOrDigit*
  ;

fragment
Letter
  : [a-zA-Z]
  ;

fragment
LetterOrDigit
  : [a-zA-Z]
  | [1-9][0-9]*
  ;

WS
    : [ \t\r\n]+ -> channel(HIDDEN)
;