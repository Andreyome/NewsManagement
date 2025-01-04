import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import './CreateNews.css'
import TagsInput from './TagsInput';

const EditNewsModal = ({ show, handleClose, newsItem, handleSave }) => {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tags, setTags] = useState([]);
    

    useEffect(() => {
        if (newsItem) {
            setTitle(newsItem.title);
            setContent(newsItem.content);
            setTags(newsItem.tagDtoResponseList.map(tag => tag.name)); // Join tags into a comma-separated string
        }
    }, [newsItem]);

    const onSave = () => {
        handleSave({
            ...newsItem,
            title,
            content,
            tagNames: tags.map(tag => tag.trim()), // Convert tags back to array
        });
        handleClose();
    };

    return (
        <Modal show={show} onHide={handleClose} centered className="modal">
            <Modal.Header closeButton style={{ borderBottom: "none" }}>
                <Modal.Title>Edit News</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="editNewsTitle">
                        <Form.Label>Title</Form.Label>
                        <Form.Control
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </Form.Group>
                    <Form.Group controlId="editNewsContent">
                        <Form.Label>Content</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                        />
                    </Form.Group>
                              <Form.Group controlId="editNewsTags">
                              <Form.Label>Tags :</Form.Label>
                        <TagsInput tagNames={tags} setTags={setTags} />
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

export default EditNewsModal;