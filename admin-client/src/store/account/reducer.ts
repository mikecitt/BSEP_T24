export const LOGIN = "account/login";
export const LOGOUT = "account/logout";

export interface AccountState {
  token: string;
  account: {
    email: string;
  };
}

const initialState: AccountState | null = null;

export function accountReducer(
  state = initialState,
  action: AccountActionTypes
): AccountState | null {
  switch (action.type) {
    case LOGIN:
      return {
        ...action.payload,
      };
    case LOGOUT:
      return null;
    default:
      return state;
  }
}

interface LoginAction {
  type: typeof LOGIN;
  payload: AccountState;
}

interface LogoutAction {
  type: typeof LOGOUT;
}

export type AccountActionTypes = LoginAction | LogoutAction;
