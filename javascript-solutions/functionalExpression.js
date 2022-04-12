"use strict";

const variable = name => (x, y, z) => name === "x" ? x : name === "y" ? y : z;

const binary  = (f) =>(a,b) => ((x, y, z) => f(a(x, y, z), b(x, y, z)));

const unary = (f) => (a) => ((x, y, z) => f(a(x, y, z)));

const cnst = value => () => value;

const add =  binary((a, b) => a + b);

const subtract = binary((a, b) => a - b);

const negate = unary(a => -a);

const multiply = binary((a, b) => a * b);

const divide = binary((a, b) => a / b);

const cube = unary(a => a * a * a);

const cuberoot =  unary(a => Math.cbrt(a));
