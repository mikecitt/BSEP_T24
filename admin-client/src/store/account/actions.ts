import { AccountActionTypes, AccountState, LOGIN, LOGOUT } from "./reducer";

export interface LoginDetails {
  email: string;
  password: string;
}

export const login = (details: AccountState) => {
  return { type: LOGIN, payload: details };
};

/*export function login(details: LoginDetails) {
  return async function loginThunk(dispatch: Dispatch) {
    try {
      const response = await axios.post(`${api}/auth/login`, details);
      dispatch({ type: LOGIN, payload: response.data });
    } catch (error) {
      if (error.response) {
        dispatch(throwError(error.response.data.localizedErrorCode));
      } else if (error.request) {
        dispatch(throwError(ErrorConstants.SERVER_API_NOT_AVAILABLE));
      }
    }
  };
}*/

export function logout(): AccountActionTypes {
  return {
    type: LOGOUT,
  };
}
