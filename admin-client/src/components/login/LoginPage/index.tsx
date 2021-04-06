import React from "react";
import { Layout, Form, Input, Button, Row, Col, notification } from "antd";
import {
  HomeOutlined,
  KeyOutlined,
  LoginOutlined,
  MailOutlined,
} from "@ant-design/icons";
import { Redirect, Link, useHistory } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { RootState } from "../../../store";
import { login } from "../../../store/account/actions";
import "./style.css";

const { Content } = Layout;

interface LoginDetails {
  email: string;
  password: string;
}

const LoginPage = () => {
  const dispatch = useDispatch();
  const history = useHistory();

  const isLogged: boolean = useSelector(
    (root: RootState) => root.account !== null
  );

  const [redirect, setRedirect] = React.useState<boolean>(false);
  const [loading, setLoading] = React.useState<boolean>(false);

  const [emailValidateStatus, setEmailValidateStatus] = React.useState<
    "" | "warning" | "success" | "error" | "validating" | undefined
  >(undefined);
  const [emailHelp, setEmailHelp] = React.useState<string | undefined>(
    undefined
  );

  const [passwordHelp, setPasswordHelp] = React.useState<string | undefined>(
    undefined
  );
  const [passwordValudateStatus, setPasswordValidateStatus] = React.useState<
    "" | "warning" | "success" | "error" | "validating" | undefined
  >(undefined);

  const onTextChange = () => {
    if (
      emailValidateStatus !== undefined ||
      passwordValudateStatus !== undefined
    ) {
      setEmailValidateStatus(undefined);
      setEmailHelp(undefined);
      setPasswordValidateStatus(undefined);
      setPasswordHelp(undefined);
    }
  };

  const handleFinish = (credentials: LoginDetails) => {
    setLoading(true);

    fetch("http://localhost:8080/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials),
    })
      .then((response) => {
        switch (response.status) {
          case 404:
            setEmailValidateStatus("warning");
            setEmailHelp("Email does not exists");
            break;
          case 403:
            setPasswordValidateStatus("warning");
            setPasswordHelp("Bad password");
            break;
          case 200:
            response.text().then((token) => {
              //dispatch(login(token));
              //history.push("/");
              notification["success"]({
                message: "Successfully logged in",
                placement: "topLeft",
              });
            });
            break;
          default:
            notification["error"]({
              message: "An error occured",
            });
            break;
        }
      })
      .catch(function () {
        notification["error"]({
          message: "Error connecting to a server",
        });
      })
      .finally(function () {
        setLoading(false);
      });
  };

  React.useEffect(() => {
    if (isLogged) {
      setRedirect(true);
    }
  }, [isLogged]);

  return redirect ? (
    <Redirect to="/" />
  ) : (
    <Layout className="layout">
      <Content>
        <Row className="contentRow">
          <Col sm={12} md={14} lg={16} className="leftCol">
            <Link to="/">
              <Button
                shape="circle"
                size="large"
                icon={<HomeOutlined />}
                className="backButton"
              />
            </Link>
          </Col>
          <Col xs={24} sm={12} md={10} lg={8} className="rightCol">
            <div className="title">
              <LoginOutlined />
              <span>Login</span>
            </div>

            <Form className="form" onFinish={handleFinish}>
              <Form.Item
                name="email"
                rules={[
                  { required: true, message: "Please enter your email" },
                  { type: "email", message: "Please enter a valid email" },
                  {
                    max: 50,
                    message: "Email must not have more than 50 characters",
                  },
                ]}
                hasFeedback
                help={emailHelp}
                validateStatus={emailValidateStatus}
              >
                <Input
                  prefix={<MailOutlined />}
                  placeholder="Email"
                  size="large"
                  onChange={onTextChange}
                />
              </Form.Item>
              <Form.Item
                name="password"
                rules={[
                  { required: true, message: "Please enter your password" },
                  {
                    min: 6,
                    message: "Password must have more than 6 characters",
                  },
                  {
                    max: 50,
                    message: "Password must not have more than 50 characters",
                  },
                ]}
                hasFeedback
                help={passwordHelp}
                validateStatus={passwordValudateStatus}
              >
                <Input.Password
                  prefix={<KeyOutlined />}
                  size="large"
                  placeholder="Password"
                  onChange={onTextChange}
                />
              </Form.Item>
              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  block
                  loading={loading}
                >
                  Log in
                </Button>
                <div className="register">
                  <Link to="/register">Don't have an account? Register</Link>
                </div>
              </Form.Item>
            </Form>
          </Col>
        </Row>
      </Content>
    </Layout>
  );
};

export default LoginPage;
