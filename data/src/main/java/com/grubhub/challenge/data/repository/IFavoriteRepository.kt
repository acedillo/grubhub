package com.grubhub.challenge.data.repository

import com.grubhub.challenge.data.entity.Favorite
import com.grubhub.persistence.repository.IBaseRepository

interface IFavoriteRepository : IBaseRepository<Favorite, Long>