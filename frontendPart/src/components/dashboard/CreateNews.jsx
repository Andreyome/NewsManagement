import React, { useState } from "react";
import { Modal, Button, Form, FloatingLabel } from "react-bootstrap";
import './CreateNews.css'
import TagsInput from './TagsInput';

const CreateNewsModal = ({ show, handleClose, handleSave }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
    const [tagNames, setTags] = useState([]);
    const [authorName,setAuthorName] = useState(localStorage.getItem('username'))

    const onSave = async () => {
      

        const newsData = {
            authorName: authorName,
      title,
      content,
      tagNames: tagNames,
    };
      try {
            const jwtToken = localStorage.getItem('token');
            const response = await fetch('http://192.168.31.125:8082/news', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`, // Add Authorization header with JWT token
              },
              body: JSON.stringify(newsData),
            });
      
            if (response.ok) {
              console.log('News successfully added!');
              handleSave(newsData); // Optionally update parent state
              handleClose(); // Close modal after save
              window.location.reload()
            } else {
              console.error('Failed to add news', await response.json());
            }
          } catch (error) {
            console.error('Error adding news:', error);
          }
  };

  return (
    <Modal show={show} onHide={handleClose} centered className="modal" >
      <Modal.Header closeButton style={{ borderBottom: "none" }}>
        <Modal.Title>New News</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <FloatingLabel controlId="floatingInputTitle" label="Title" className="mb-3">
            <Form.Control
              type="text"
              placeholder="Enter the title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </FloatingLabel>

          <FloatingLabel controlId="floatingTextareaContent" label="Content" className="mb-3">
            <Form.Control
              as="textarea"
              placeholder="Enter the content"
              style={{ height: "100px" }}
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
          </FloatingLabel>
          <Form.Group controlId="editNewsTags">
            <Form.Label>Tags</Form.Label>
            <TagsInput tagNames={tagNames} setTags={setTags} />
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer style={{ borderTop: "none" }} className="d-flex justify-content-between">
        <Button variant="secondary" onClick={handleClose}>
          Cancel
        </Button>
        <Button variant="primary" onClick={onSave}>
          Save
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default CreateNewsModal;