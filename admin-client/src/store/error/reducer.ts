export const THROW_ERROR = 'error/throwError';
export const CLEAR_ERROR = 'error/clearError';

export interface ErrorState {
  errorCode: number | null;
}

const initialState: ErrorState = { errorCode: null };

export function errorReducer(
  state = initialState,
  action: ErrorActionTypes
): ErrorState {
  switch (action.type) {
    case THROW_ERROR:
      return {
        errorCode: action.payload
      };
    case CLEAR_ERROR:
      return {
        errorCode: null
      };
    default:
      return state;
  }
}

interface ThrowErrorAction {
  type: typeof THROW_ERROR;
  payload: number;
}

interface ClearErrorAction {
  type: typeof CLEAR_ERROR;
}

export type ErrorActionTypes = ThrowErrorAction | ClearErrorAction;
