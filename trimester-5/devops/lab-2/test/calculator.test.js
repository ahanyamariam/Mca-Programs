'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const { add, subtract, multiply, divide } = require('../src/calculator');

test('add returns the sum', () => {
  assert.equal(add(2, 3), 5);
  assert.equal(add(-1, 1), 0);
});

test('subtract returns the difference', () => {
  assert.equal(subtract(5, 3), 2);
});

test('multiply returns the product', () => {
  assert.equal(multiply(4, 6), 24);
  assert.equal(multiply(5, 0), 0);
});

test('divide returns the quotient', () => {
  assert.equal(divide(8, 2), 4);
});

test('divide by zero throws RangeError', () => {
  assert.throws(() => divide(1, 0), RangeError);
});

test('non-numeric input throws TypeError', () => {
  assert.throws(() => add('1', 2), TypeError);
  assert.throws(() => multiply(1, NaN), TypeError);
});
