'use strict';

const { add, subtract, multiply, divide } = require('./calculator');

if (require.main === module) {
  console.log('2 + 3 =', add(2, 3));
  console.log('5 - 1 =', subtract(5, 1));
  console.log('4 * 6 =', multiply(4, 6));
  console.log('8 / 2 =', divide(8, 2));
}

module.exports = { add, subtract, multiply, divide };
