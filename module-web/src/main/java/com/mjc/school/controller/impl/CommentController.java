package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.Hateoas.HateoasHelper;
import com.mjc.school.service.CommentServInterface;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RestController
@RequestMapping(value = "/comment", produces = {"application/JSON"})
@Api(produces = "application/JSON", value = "CRUD operations with Comments")
public class CommentController implements BaseController<CommentDtoRequest, CommentDtoResponse, Long> {
    private final CommentServInterface commentService;

    @Autowired
    public CommentController(CommentServInterface commentService) {
        this.commentService = commentService;
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all comments", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all comments"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Internal resource not found")
    })
    public List<CommentDtoResponse> readAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
                                            @RequestParam(value = "sortBy", required = false, defaultValue = "content:desc") String sortBy) {
        return commentService.readAll(page, limit, sortBy);
    }

    @Override
    @GetMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get comments by Id", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comment by Id"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Internal resource not found")
    })
    public EntityModel<CommentDtoResponse> readById(@PathVariable Long id) {
        EntityModel<CommentDtoResponse> result = EntityModel.of(commentService.readById(id));
        HateoasHelper.addCommentLinks(result);
        return result;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a comment"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "User does not have required role for the operation"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Internal resource not found")
    })
    public EntityModel<CommentDtoResponse> create(@RequestBody CommentDtoRequest createRequest) {
        EntityModel<CommentDtoResponse> result = EntityModel.of(commentService.create(createRequest));
        HateoasHelper.addCommentLinks(result);
        return result;
    }

    @Override
    @PatchMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated comment"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "User does not have required role for the operation"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Internal resource not found")
    })
    public EntityModel<CommentDtoResponse> update(@PathVariable Long id, @RequestBody CommentDtoRequest updateRequest) {
        EntityModel<CommentDtoResponse> result = EntityModel.of(commentService.update(id, updateRequest));
        HateoasHelper.addCommentLinks(result);
        return result;
    }

    @Override
    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete comment")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted a comment"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "User does not have required role for the operation"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 404, message = "Internal resource not found")

    })
    public void deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
    }

}
