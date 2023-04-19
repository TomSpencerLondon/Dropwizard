package org.example.controller;

import com.codahale.metrics.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.jetty.http.HttpStatus;
import org.example.model.Part;
import org.example.service.PartsService;

import java.util.List;

@Path("/parts")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class PartsResource {
    private final PartsService partsService;;

    public PartsResource(PartsService partsService) {
        this.partsService = partsService;
    }

    @GET
    @Timed
    public Representation<List<Part>> getParts() {
        return new Representation<List<Part>>(HttpStatus.OK_200, partsService.getParts());
    }

    @GET
    @Timed
    @Path("{id}")
    public Representation<Part> getPart(@PathParam("id") final int id) {
        return new Representation<Part>(HttpStatus.OK_200, partsService.getPart(id));
    }

    @POST
    @Timed
    public Representation<Part> createPart(@NotNull @Valid final Part part) {
        return new Representation<Part>(HttpStatus.OK_200, partsService.createPart(part));
    }

    @PUT
    @Timed
    @Path("{id}")
    public Representation<Part> editPart(@NotNull @Valid final Part part,
                                         @PathParam("id") final int id) {
        part.setId(id);
        return new Representation<Part>(HttpStatus.OK_200, partsService.editPart(part));
    }

    @DELETE
    @Timed
    @Path("{id}")
    public Representation<String> deletePart(@PathParam("id") final int id) {
        return new Representation<String>(HttpStatus.OK_200, partsService.deletePart(id));
    }
}
