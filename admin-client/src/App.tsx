import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";

import LoginPage from "./components/login/LoginPage";

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <Route path="/login">
          <LoginPage />
        </Route>
      </Switch>
    </BrowserRouter>
  );
}

export default App;
