"use strict";

class Maybe {
    static #EMPTY = new Maybe(null);
    constructor(value) {
        this.value = value;
    }

    static of = (value) =>
        (value === null) ? this.#EMPTY : new Maybe(value);

    #isNull() {
        return this.value === null;
    }

    map(fn) {
        return this.#isNull() ?
            this.#EMPTY :
            Maybe.of(fn(this.value));
    }

    flatMap(fn) {
        return this.#isNull() ?
            this.#EMPTY :
            fn(this.value);
    }
}

class Pipe {
    constructor(left = 0, right = 0) {
        this.left = left;
        this.right = right;
    }

    increaseLeft(amount) {
        return new Pipe(this.left + amount, this.right);
    }
    increaseRight(amount) {
        return new Pipe(this.left, this.right + amount);
    }
    isImbalanced() {
        return Math.abs(this.left - this.right) > 3;
    }
}

function increaseLeft(pipe, amount) {
    const newPipe = pipe.increaseLeft(amount);
    return newPipe.isImbalanced() ? Maybe.of(null)
        : Maybe.of(newPipe);
}

function increaseRight(pipe, amount) {
    const newPipe = pipe.increaseRight(amount);
    return newPipe.isImbalanced() ? Maybe.of(null)
        : Maybe.of(newPipe);
}

function main() {
    const maybe = Maybe.of(new Pipe())
        .flatMap((curr) => increaseLeft(curr, 2))
        .flatMap((curr) => increaseRight(curr, 2))
        .flatMap((curr) => increaseLeft(curr, 2))
    console.log(maybe);
}

main();
