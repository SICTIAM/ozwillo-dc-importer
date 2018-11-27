import Vue from 'vue'
import distanceInWords from 'date-fns/distance_in_words'
import fr from 'date-fns/locale/fr'

Vue.filter('dateDistanceInWords', value => {
    return distanceInWords(new Date(value), new Date(), {locale: fr})
})
