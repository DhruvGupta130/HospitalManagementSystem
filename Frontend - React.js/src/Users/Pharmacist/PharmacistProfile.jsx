import { useEffect, useState } from "react";
import { fetchPharmacistProfileData } from "./fetchPharmacistProfileData";
import { Card, Spin, Alert, Typography, Button, Modal, Form, Input, message, Select, Row, Col } from "antd";
import { MailOutlined, ManOutlined, PhoneOutlined, UserOutlined, UserSwitchOutlined, WomanOutlined } from "@ant-design/icons";
import axios from "axios";
import { hospitalURL } from "../../Api & Services/Api.js";
import { formatGender } from "../../Api & Services/Services.js";

const { Title, Text } = Typography;

const PharmacistProfile = () => {
  const [Pharmacist, setPharmacist] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isOpenModal, setIsOpenModal] = useState(false);
  const [newData, setNewData] = useState({
    firstName: "",
    lastName: "",
    gender: "",
    email: "",
    mobile: "",
  });

  useEffect(() => {
    fetchPharmacistProfileData(setPharmacist, setLoading, setError);
  }, []);

  useEffect(() => {
    if (Pharmacist) {
      setNewData({
        firstName: Pharmacist.firstName,
        lastName: Pharmacist.lastName,
        gender: Pharmacist.gender,
        email: Pharmacist.email,
        mobile: Pharmacist.mobile,
      });
    }
  }, [Pharmacist]);  

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const openModal = () => setIsOpenModal(true);
  const closeModal = () => setIsOpenModal(false);

  const updateProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.put(`${hospitalURL}/Pharmacist`, newData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      message.success(response.data.message);
      closeModal();
      await fetchPharmacistProfileData(setPharmacist, setLoading, setError);
    } catch (error) {
      message.error(error?.response?.data?.message || "Failed to update Pharmacist");
    }
  };

  return (
    <Row justify="center" style={{ marginTop: 30, padding: 20 }}>
      <Col xs={24} sm={20} md={16} lg={12}>
        <Card
          title={<Title level={3} style={{ textAlign: "center", color: "#1d3557" }}>Pharmacist Profile</Title>}
          bordered={false}
          style={{
            borderRadius: "16px",
            boxShadow: "0 8px 16px rgba(0, 0, 0, 0.1)",
            backgroundColor: "#f8fafc",
          }}
        >
          {loading ? (
            <Spin tip="Loading Pharmacist..." size="large" style={{ display: "block", margin: "auto" }} />
          ) : error ? (
            <Alert message={error} type="error" showIcon />
          ) : (
            Pharmacist ? (
              <div>
                <Row gutter={[16, 16]} justify="space-between">
                  <Col xs={24} sm={12}>
                    <Card bordered={false} style={{ backgroundColor: "#ffffff", borderRadius: "12px" }}>
                      <p style={{ fontSize: "18px" }}>
                        <UserOutlined style={{ marginRight: "8px", color: "#457b9d" }} />
                        <Text strong style={{ fontSize: "18px" }}>Name:</Text> {Pharmacist.fullName}
                      </p>
                      <p style={{ fontSize: "18px" }}>
                        {Pharmacist.gender === "MALE" ? <ManOutlined style={{ color: "#2a9d8f" }} /> : <WomanOutlined style={{ color: "#e76f51" }} />}
                        <Text strong style={{ marginLeft: "8px", fontSize: '18px' }}>Gender:</Text> {formatGender(Pharmacist.gender)}
                      </p>
                    </Card>
                  </Col>

                  <Col xs={24} sm={12}>
                    <Card bordered={false} style={{ backgroundColor: "#ffffff", borderRadius: "12px" }}>
                      <p style={{ fontSize: "18px" }}>
                        <MailOutlined style={{ marginRight: "8px", color: "#457b9d" }} />
                        <Text strong style={{ fontSize: "18px" }}>Email:</Text> {Pharmacist.email}
                      </p>
                      <p style={{ fontSize: "18px" }}>
                        <PhoneOutlined style={{ marginRight: "8px", color: "#457b9d" }} />
                        <Text strong style={{ fontSize: "18px" }}>Mobile:</Text> {Pharmacist.mobile}
                      </p>
                    </Card>
                  </Col>
                </Row>

                <Button
                  type="primary"
                  onClick={openModal}
                  style={{
                    marginTop: "16px",
                    width: "100%",
                    background: "linear-gradient(90deg, #457b9d, #1d3557)",
                    border: "none",
                    fontSize: "16px",
                  }}
                >
                  Update Profile
                </Button>
              </div>
            ) : (
              <Text>No Pharmacist data available.</Text>
            )
          )}
        </Card>

        {/* Update Profile Modal */}
        <Modal
          title={<Title level={4} style={{ color: "#457b9d" }}>Update Profile</Title>}
          open={isOpenModal}
          onCancel={closeModal}
          footer={null}
        >
          <Form layout="vertical">
            <Row gutter={[16, 16]}>
              {["firstName", "lastName"].map((field) => (
                <Col xs={24} sm={12} key={field}>
                  <Form.Item label={field.charAt(0).toUpperCase() + field.slice(1)}>
                    <Input name={field} value={newData[field]} onChange={handleInputChange} />
                  </Form.Item>
                </Col>
              ))}

              <Col xs={24}>
                <Form.Item label="Gender">
                  <Select
                    name="gender"
                    value={newData.gender}
                    onChange={(value) => handleInputChange({ target: { name: "gender", value } })}
                    style={{ width: "100%" }}
                  >
                    <Select.Option value="MALE">Male</Select.Option>
                    <Select.Option value="FEMALE">Female</Select.Option>
                    <Select.Option value="OTHER">Other</Select.Option>
                  </Select>
                </Form.Item>
              </Col>

              {["email", "mobile"].map((field) => (
                <Col xs={24} key={field}>
                  <Form.Item label={field.charAt(0).toUpperCase() + field.slice(1)}>
                    <Input name={field} value={newData[field]} onChange={handleInputChange} />
                  </Form.Item>
                </Col>
              ))}

              <Col xs={24}>
                <Button
                  type="primary"
                  icon={<UserSwitchOutlined />}
                  onClick={updateProfile}
                  style={{
                    width: "100%",
                    background: "linear-gradient(90deg, #457b9d, #1d3557)",
                    border: "none",
                    fontSize: "16px",
                  }}
                >
                  Update Profile
                </Button>
              </Col>
            </Row>
          </Form>
        </Modal>
      </Col>
    </Row>
  );
};

export default PharmacistProfile;
