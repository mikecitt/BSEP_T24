import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunkMiddleware from 'redux-thunk';
import { composeWithDevTools } from 'redux-devtools-extension';

import { accountReducer } from './account/reducer';
import { errorReducer } from './error/reducer';

const rootReducer = combineReducers({
  account: accountReducer,
  error: errorReducer
});

export type RootState = ReturnType<typeof rootReducer>;

const composedEnhancer = composeWithDevTools(applyMiddleware(thunkMiddleware));

const store = createStore(rootReducer, composedEnhancer);
export default store;
