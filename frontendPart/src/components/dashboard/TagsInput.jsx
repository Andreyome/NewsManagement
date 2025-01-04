import React, { useState, useEffect } from 'react';
import { WithContext as ReactTags } from 'react-tag-input';
import './TagsInput.css'; // You can style the tags as needed

const KeyCodes = {
  comma: 188,
  enter: 13,
};

const delimiters = [KeyCodes.comma, KeyCodes.enter];

const TagsInput = ({ tagNames, setTags }) => {
  const [tags, setInternalTags] = useState(tagNames.map(tag => ({ id: tag, text: tag })));
  const [suggestions, setSuggestions] = useState([]);

  useEffect(() => {
    // Update internal tags when tagNames prop changes
    setInternalTags(tagNames.map(tag => ({ id: tag, text: tag })));
  }, [tagNames]);

  useEffect(() => {
    // Fetch tags from API
    const fetchTags = async () => {
      try {
        const response = await fetch('http://192.168.31.125:8082/tag?limit=10000&page=1&sortBy=name%3Adesc');
        const data = await response.json();
        const tagSuggestions = data.map(tag => ({ id: tag.id.toString(), text: tag.name }));
        setSuggestions(tagSuggestions);
      } catch (error) {
        console.error('Error fetching tags:', error);
      }
    };
    fetchTags();
  }, []);

  const handleDelete = (i) => {
    const newTags = tags.filter((_, index) => index !== i);
    setInternalTags(newTags);
    setTags(newTags.map(tag => tag.text));
  };

  const handleAddition = (tag) => {
    const newTags = [...tags, tag];
    setInternalTags(newTags);
    setTags(newTags.map(tag => tag.text));
  };

  return (
    <ReactTags
      tags={tags}
      suggestions={suggestions}
      handleDelete={handleDelete}
      handleAddition={handleAddition}
      delimiters={delimiters}
      placeholder = "Press enter to add tag"
    />
  );
};

export default TagsInput;