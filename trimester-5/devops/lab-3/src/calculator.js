'use strict';

function assertNumber(value, name) {
  if (typeof value !== 'number' || Number.isNaN(value)) {
    throw new TypeError(`${name} must be a number`);
  }
}

function add(a, b) {
  assertNumber(a, 'a');
  assertNumber(b, 'b');
  return a + b;
}

function subtract(a, b) {
  assertNumber(a, 'a');
  assertNumber(b, 'b');
  return a - b;
}

function multiply(a, b) {
  assertNumber(a, 'a');
  assertNumber(b, 'b');
  return a * b;
}

function divide(a, b) {
  assertNumber(a, 'a');
  assertNumber(b, 'b');
  if (b === 0) {
    throw new RangeError('Cannot divide by zero');
  }
  return a / b;
}

module.exports = { add, subtract, multiply, divide };
