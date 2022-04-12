"use strict";


function Operation(...args) {
    this.args = args;
}

Operation.prototype.evaluate = function (x, y, z) {
    return this.f(...this.args.map(arg => arg.evaluate(x, y, z)));
};
Operation.prototype.toString = function () {
    return this.args.join(" ") + " " + this.sigh;
};
Operation.prototype.prefix = function () {
    return "(" + this.sigh + " " + this.args.map(arg => arg.prefix()).join(" ") + ")";
};

function Const(value) {
    this.value = value;
}

Const.prototype.evaluate = function (x, y, z) {
    return this.value;
};
Const.prototype.toString = function () {
    return this.value.toString();
};
Const.prototype.prefix = function () {
    return this.value.toString();
};


function Variable(value) {
    this.value = value;
}

Variable.prototype.evaluate = function (x, y, z) {
    return this.value === "x" ? x : this.value === 'y' ? y : z;
};
Variable.prototype.toString = function () {
    return this.value.toString();
};
Variable.prototype.prefix = function () {
    return this.value.toString();
};

function operation(f, sigh) {
    function newOp(...args) {
        Operation.call(this, ...args);
    }
    newOp.prototype = Object.create(Operation.prototype);
    newOp.prototype.f = f;
    newOp.prototype.sigh = sigh;
    return newOp;
}

const Add = operation((a, b) => (a + b), "+");

const Negate = operation((value) => (-value), "negate");

const ArcTan = operation((value) => (Math.atan(value)), "atan");

const Exp = operation((value) => (Math.exp(value)), "exp");

const Subtract = operation((a, b) => (a - b), "-");

const Multiply = operation((a, b) => (a * b), "*");

const Divide = operation((a, b) => (a / b), "/");

const Min3 = operation(Math.min, "min3");

const Max5 = operation(Math.max, "max5");

function ParseError(message) {
    this.message = message;
}

ParseError.prototype = Object.create(Error.prototype);
ParseError.prototype.constructor = ParseError;
ParseError.prototype.name = "ParseError";

const NameOperation = {
    "negate": Negate,
    "atan": ArcTan,
    "exp": Exp,
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide
};
const CountOperation = {
    "negate": 1,
    "atan": 1,
    "exp": 1,
    "+": 2,
    "-": 2,
    "*": 2,
    "/": 2
};


function parsePrefix(string) {
    let i = 0;
    let skob = 0;

    function writeExcep(i1) {
        throw new ParseError(string.slice(0, i1) + "No operand or Unexpected Operation ---->>" + string.slice(i1));
    }

    function cnstVar(string) {
        skipWhitespace(string);
        if (string[i] === "x" || string[i] === "y" || string[i] === "z") {
            return new Variable(string[i++]);
        }
        if (!isNaN(string[i]) || string[i] === "-") {
            let a = "";
            a += string[i++];
            while (!isNaN(string[i]) && string[i] !== " ") {
                a += string[i++];
            }
            return new Const(Number(a));
        }
        writeExcep(i - 1);
    }

    function operation(string) {
        skipWhitespace(string);
        let i1 = i;
        let sign = string[i];
        i++;
        if (sign === "n" || sign === "a" || sign === "e") {
            while (string[i] !== " " && string[i] !== "(" && i < string.length) {
                sign += string[i++];
            }
        }
        if (!(sign in NameOperation)) {
            writeExcep(i1)
        }
        if (string[i] !== " " && string[i] !== "(") {
            throw new ParseError("No operand!")
        }
        let argn = [];
        for (let j = 0; j < CountOperation[sign]; j++) {
            argn.push(result(string))
        }
        return new NameOperation[sign](...argn.values());

    }

    function result(string) {
        skipWhitespace(string);
        if (string[i] === "(") {
            skob++;
            i++;
            let a = operation(string);
            skipWhitespace(string);
            if (string[i] === ")") {
                skob--;
                i++;
            }
            skipWhitespace(string);
            return a;
        } else {
            let pref = cnstVar(string);
            skipWhitespace(string);
            return pref
        }
    }

    function skipWhitespace(string) {
        while (string[i] === " ") {
            i++;
        }
    }

    let res = result(string);
    skipWhitespace(string);
    if (i === string.length && skob === 0) {
        return res;
    } else {
        if (skob > 0) {
            throw new ParseError("Missing )");
        } else if (skob < 0) {
            throw new ParseError("to many (");
        }
        throw new ParseError(string.slice(0, i - 1) + "Unexpected symbol ---->> " + string.slice(i - 1));
    }

}
