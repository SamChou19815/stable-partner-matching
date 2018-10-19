/**
 * Ignore is used as a callback to ignore things.
 */
export const ignore: () => void = () => {
};

/**
 * Run a block of code.
 *
 * @param {() => void} action the code to run.
 */
export function run<T>(action: () => T): T {
  return action();
}

/**
 * Asynchronously run a block of code.
 *
 * @param {() => Promise<void>} action the code to run asynchronously.
 */
export function asyncRun(action: () => Promise<void>): void {
  (async () => action())();
}

/**
 * Run [f] after a short delay.
 *
 * @param {() => void} action the function to run after the short delay.
 */
export function shortDelay(action: () => void): void {
  setTimeout(action, 100);
}

/**
 * Wait for ms.
 *
 * @param {number} ms number of milliseconds to wait.
 * @returns {Promise<void>} promise when done.
 */
export async function wait(ms: number): Promise<void> {
  await new Promise(resolve => {
    setTimeout(resolve, ms);
  });
}

/**
 * An array of all possible hours.
 * @type {number[]}
 */
export const possibleHoursArray: number[] = run(() => {
  const arr = Array<number>(24);
  for (let i = 0; i < 24; i++) {
    arr[i] = i;
  }
  return arr;
});
