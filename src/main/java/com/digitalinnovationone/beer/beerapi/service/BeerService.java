package com.digitalinnovationone.beer.beerapi.service;

import com.digitalinnovationone.beer.beerapi.dto.BeerDTO;
import com.digitalinnovationone.beer.beerapi.entity.Beer;
import com.digitalinnovationone.beer.beerapi.exception.BeerAlreadyRegisteredException;
import com.digitalinnovationone.beer.beerapi.exception.BeerNotFoundException;
import com.digitalinnovationone.beer.beerapi.exception.BeerStockExceededException;
import com.digitalinnovationone.beer.beerapi.mapper.BeerMapper;
import com.digitalinnovationone.beer.beerapi.repository.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beer);
        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));
        return beerMapper.toDTO(foundBeer);
    }

    public List<BeerDTO> listAll() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrementStock = verifyIfExists(id);
        int quantityStockAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityStockAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(quantityStockAfterIncrement);
            Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
            return beerMapper.toDTO(incrementedBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }

    public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToDecrementStock = verifyIfExists(id);
        int quantityStockAfterDecrement = beerToDecrementStock.getQuantity() - quantityToDecrement;
        if (quantityStockAfterDecrement >= 0){
            beerToDecrementStock.setQuantity(quantityStockAfterDecrement);
            Beer decrementedBeerStock = beerRepository.save(beerToDecrementStock);
            return beerMapper.toDTO(decrementedBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToDecrement);
    }
}
