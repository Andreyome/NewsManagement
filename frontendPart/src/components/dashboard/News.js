import React, { useState, useEffect,} from "react";
import { Card, Button, Row, Col, Pagination, Form, Modal } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import './News.css'
import CreateNewsModal from "./CreateNews";
import EditNewsModal from "./EditNewsModal";
import Delete from "./Delete.webp";

const News = ({news}) => {
    const [newsList, setNewsList] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState("");
    const BASE_URL = "http://192.168.31.125:8082";

    const location = useLocation();
    const navigate = useNavigate();

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedNewsId, setSelectedNewsId] = useState(null);

    const [sortOption, setSortOption] = useState("createDate:desc");
    const [pageSize, setPageSize] = useState(10)

    const [showEditModal, setShowEditModal] = useState(false); // State for Edit modal
    const [selectedNewsItem, setSelectedNewsItem] = useState(null);
    // 1. Initialize state from URL parameters only on the first load
    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        setCurrentPage(Number(queryParams.get("page") || 1));
        setSearchQuery(queryParams.get("query") || "");
        setSortOption(queryParams.get("sort") || "createDate:desc");
        setPageSize(Number(queryParams.get("limit") || 10));
        console.log(queryParams.get("sort"),queryParams.get("limit"),queryParams.get("query"))
    }, []);
        // Update the URL when state changes
        useEffect(() => {
            const queryParams = new URLSearchParams(location.search);
            queryParams.set("page", currentPage);
            queryParams.set("query", searchQuery);
            queryParams.set("sort", sortOption);
            queryParams.set("limit", pageSize);
        
            navigate({ search: queryParams.toString() }, { replace: true });
        }, [currentPage, searchQuery, sortOption, pageSize]);
    
    const handleSortChange = (event) => {
        setSortOption(event.target.value);
        fetchNews(1, searchQuery, event.target.value); // Refetch with new sorting
    };
    const handlePageSizeChange = (event) => {
        setPageSize(event.target.value)
        fetchNews(1, searchQuery, sortOption,event.target.value);
    }

    const handleDeleteClick = (id) => {
        setShowDeleteModal(true);
        setSelectedNewsId(id);
    };
    const handleEditClick = (newsItem) => {
        console.log('Editing news item:', newsItem);
        setSelectedNewsItem(newsItem); // Set the selected news item
        setShowEditModal(true); // Show the edit modal
    };

    const handleSaveEdit = async (updatedNews) => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`${BASE_URL}/news/${updatedNews.id}`, {
                method: 'PATCH', 
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    authorName: updatedNews.authorDto.name, 
                    content: updatedNews.content,
                    title: updatedNews.title,
                    tagNames: updatedNews.tagNames,
                }),
            });
            if (response.ok) {
                console.log('News updated');
                fetchNews(currentPage); // Refresh news list
                setShowEditModal(false); // Close the modal
            } else {
                console.error('Error updating news');
            }
        } catch (error) {
            console.error('Update request failed', error);
        }
    };

    const handleDeleteNews = async () => {
        const token = localStorage.getItem('token');
        try {
          const response = await fetch(`${BASE_URL}/news/${selectedNewsId}`, {
            method: 'DELETE',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          });
            if (response.ok) {
                window.location.reload()
            console.log('News deleted');
            // Refresh or remove news from the list
          } else {
            console.error('Error deleting news');
          }
        } catch (error) {
          console.error('Delete request failed', error);
        }
        setShowDeleteModal(false);
      };

    const handleSave = (newsData) => {
        // API call to save the news data
        console.log("News created:", newsData);
      };


    // Helper to extract plain text and hashtags
    const parseSearchQuery = (query) => {
        const tags = [];
        let plainText = "";

        query.split(" ").forEach((word) => {
            if (word.startsWith("#")) {
                tags.push(word.substring(1)); // Remove the "#" and store tag
            } else {
                plainText += word + " "; // Add word to plain text query
            }
        });

        return { plainText: plainText.trim(), tags };
    };

    const fetchNews = async (page, query = searchQuery,sort = sortOption,limit = pageSize) => {
        try {
            let endpoint;
          if (query.trim() === "") {
            // If search query is empty, fetch all news with pagination
            const endpoint = `${BASE_URL}/news?limit=${pageSize}&page=${currentPage}&sortBy=${sortOption}`;
            const response = await fetch(endpoint);
            const data = await response.json();
            console.log("Requesting all news:", endpoint);
      
            setNewsList(data.items); // Assuming your API response has an 'items' field
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
          } else {
            // If there's a search query, use the search by params method
            const { plainText, tags } = parseSearchQuery(query);
            const params = new URLSearchParams({
              page: page,
              Title: plainText,
              Content: plainText,
                Tag_Names: tags.join(","),
                sortBy: sort,
                limit: limit,
            });
            const endpoint = `${BASE_URL}/news/byParams?${params}`;
            const response = await fetch(endpoint);
            const data = await response.json();
            console.log("Requesting filtered news:", endpoint);
      
            setNewsList(data.items);
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
          }
        } catch (error) {
          console.error("Error fetching news:", error);
        }
    };

    useEffect(() => {
        fetchNews(currentPage, searchQuery);
    }, [currentPage]);

    useEffect(() => {
        if (searchQuery === "") {
            fetchNews(1); // Fetch all news when the search query is cleared
        }
    }, [searchQuery]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleSearchChange = (event) => {
        setSearchQuery(event.target.value);
    };

    const handleSearchSubmit = (event) => {
        event.preventDefault(); // Prevent default form submission
        setCurrentPage(1); // Reset to the first page
      
        if (searchQuery.trim() === "") {
          fetchNews(1); // Fetch all news without search filters when searchQuery is empty
        } else {
          fetchNews(1, searchQuery); // Fetch news with the search query
        }
      };

    const renderPaginationItems = () => {
        const items = [];
        const totalDisplayedPages = 8;
        let startPage = Math.max(currentPage - Math.floor(totalDisplayedPages / 2), 1);
        let endPage = Math.min(startPage + totalDisplayedPages - 1, totalPages);

        if (endPage - startPage < totalDisplayedPages - 1) {
            startPage = Math.max(endPage - totalDisplayedPages + 1, 1);
        }

        if (currentPage > 1) {
            items.push(<Pagination.First key="first" onClick={() => handlePageChange(1)} />);
        }

        if (currentPage > 1) {
            items.push(<Pagination.Prev key="prev" onClick={() => handlePageChange(currentPage - 1)} />);
        }

        for (let number = startPage; number <= endPage; number++) {
            items.push(
                <Pagination.Item key={number} active={number === currentPage} onClick={() => handlePageChange(number)}>
                    {number}
                </Pagination.Item>
            );
        }

        if (currentPage < totalPages) {
            items.push(<Pagination.Next key="next" onClick={() => handlePageChange(currentPage + 1)} />);
        }

        if (currentPage < totalPages) {
            items.push(<Pagination.Last key="last" onClick={() => handlePageChange(totalPages)} />);
        }

        return items;
    };

    return (
        <div className="container mt-0">
            
            <div className="news-header d-flex justify-content-between align-items-center">
            <Form onSubmit={handleSearchSubmit} className="mb-3 w-100">
                <Form.Group controlId="search">
                    <Form.Control
                        type="text"
                        placeholder="Search News by title, content, or #tags"
                        value={searchQuery}
                        onChange={handleSearchChange}
                    />
                </Form.Group>
            </Form>            
            <button className="add-news-btn btn btn-primary mb-3" onClick={()=> setShowCreateModal(true)} >Add News</button>
            <CreateNewsModal
        show={showCreateModal}
        handleClose={() => setShowCreateModal(false)}
        handleSave={handleSave}
      />
            </div>
            <div className="header-container">
            <h2>{totalElements} News</h2>
            <select className="create-date-dropdown"  onChange={handleSortChange} >
    <option value="createDate:desc">Date Created (Newest)</option>
    <option value="createDate:asc">Date Created (Oldest)</option>
    <option value="author:asc">Author Name (A-Z)</option>
    <option value="author:desc">Author Name (Z-A)</option>
            </select>
            </div>
            <Row>
                {(newsList && Array.isArray(newsList)) ? (newsList.map((news) => (
                    <Col key={news.id} md={4}>
                        <Card className="mb-4 shadow-sm">
                            <Card.Body>
                                <Card.Title>{news.title}</Card.Title>
                                <Card.Subtitle className="mb-2 text-muted">
                                    {new Date(news.createDate).toLocaleDateString()} {new Date(news.lastUpdateDate).toLocaleDateString()}
                                </Card.Subtitle>
                                <Card.Text>{news.authorDto.name}</Card.Text>
                                <div>
                                    {news.tagDtoResponseList.map((tag) => (
                                        <Button key={tag.id} variant="outline-primary" size="sm"  className="ms-1 mb-1">
                                            {tag.name}
                                        </Button>
                                    ))}
                                </div>
                                <div className="mt-2">
                                    <Button variant="warning" size="sm"  onClick={() => handleEditClick(news)}>
                                        <i className="fa fa-pencil"></i>
                                    </Button>
                                    <Button variant="danger" size="sm" onClick={() => handleDeleteClick(news.id)}className="ms-1">
                                        <i className="fa fa-trash"></i>
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))
            ) : (
                <p>No news available.</p>
              )}
            </Row>
            <div className="header-container">

            <Pagination className="justify-content-center">
                {renderPaginationItems()}
                </Pagination>
                        <select className="create-date-dropdown"  onChange={handlePageSizeChange} >
    <option value="10">10</option>
    <option value="20">20</option>
    <option value="50">50</option>
            </select>
            </div>
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered  dialogClassName="custom-modal-style">
        <Modal.Header closeButton style={{ borderBottom: "none" }}></Modal.Header >
                <Modal.Body closeButton className="d-flex flex-column align-items-center">
                    <img src={Delete} alt="description" style={{ width: "130px", height: "auto" }} centered />
                    <p className="mt-3 " style={{ fontSize: "1.4rem", fontWeight: "bold" }}>Do you really want to delete this news?</p></Modal.Body>
        <Modal.Footer style={{ borderTop: "none" }} className="d-flex justify-content-between">
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDeleteNews}>
            Delete
          </Button>
        </Modal.Footer>
            </Modal>
            <EditNewsModal
    show={showEditModal}
    handleClose={() => setShowEditModal(false)}
    newsItem={selectedNewsItem} // Pass the selected news item
    handleSave={handleSaveEdit} // Pass the save function
/>
        </div>
    );
};

export default News;