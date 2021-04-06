import { THROW_ERROR, CLEAR_ERROR, ErrorActionTypes } from './reducer';

export function throwError(error: number): ErrorActionTypes {
  return {
    type: THROW_ERROR,
    payload: error
  };
}
export function clearError(): ErrorActionTypes {
  return {
    type: CLEAR_ERROR
  };
}
