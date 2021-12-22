package com.digitalinnovationone.beer.beerapi.mapper;

import com.digitalinnovationone.beer.beerapi.dto.BeerDTO;
import com.digitalinnovationone.beer.beerapi.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}