<template>
    <div class="container">
        <h2>Clone selected connector</h2>
        <form>
            <div class="form-group row">
                <label for="application-name-input" class="col-sm-3 col-form-label col-form-label-sm">Application: </label>
                <input type="text" id="application-name-input" v-model="selectedConnector.applicationName" disabled/>
            </div>
            <div class="form-group row">
                <label for="new-organization-claimer" class="col-sm-3 col-form-label col-form-label-sm">
                    Collectivité: 
                </label>
                <vue-bootstrap-typeahead
                    id="new-organization-claimer"
                    ref="neworganizationclaimer"
                    placeholder="Find an Organization"
                    :minMatchingChars="0"
                    v-model="organizationSearch"
                    :data="organizations"
                    :serializer="displayingResultOfOrganizationSearch"
                    :append="organizationSelected.siret"
                    @hit="organizationSelected = $event"    
                />
            </div>
            <input type="button" value="Create" :disabled="disabled" @click="cloneConnector"/>
            <input type="button" value="Cancel" @click="backToConnectorsManagament"/>
        </form>
    </div>
</template>

<script>

import VueBootstrapTypeahead from 'vue-bootstrap-typeahead'
import axios from 'axios'
import debounce from 'lodash/debounce'

export default {
    name: 'cloneConnector',
    components: {
        VueBootstrapTypeahead
    },
    data() {
        return {
            selectedConnector: {
                id: '',
                applicationName: '',
                baseUrl: '',
                organizationSiret: ''
            },
            organizations: [],
            organizationSearch: '',
            organizationSelected: {},
            errors: [],
            duplicateErrorMessage: ''
        }
    },
    watch: {
        organizationSearch: debounce(function(name) { this.getOrganizations(name) }, 500),
        organizationSelected: {
            handler (val, oldVal){
                this.selectedConnector.organizationSiret = val.siret
                this.$refs.neworganizationclaimer.$data.inputValue = val.denominationUniteLegale
            },
            deep: true
        }
    },
    computed: {
        disabled (){
            return Object.keys(this.organizationSelected).length === 0
        }
    },
    created (){
        this.selectedConnector = {
            id: this.$route.params.id,
            applicationName: this.$route.params.appName,
            baseUrl: ''
        }
    },
    methods: {
        getOrganizations (name){
            if(this.organizationSearch === '') this.organizationSelected = {}
            axios.get("/dc/organizations", {params: {name: name}})
            .then(response => {
                this.organizations = response.data
            })
            .catch(e => {
                this.errors.push(e)
            })
        },
        displayingResultOfOrganizationSearch(organization){
            return `${organization.denominationUniteLegale}, ${organization.siret}`
        },
        cloneConnector (){
            axios.post(`/configuration/connectors/${this.selectedConnector.id}/clone`, this.selectedConnector)
            .then(response => {
                this.$router.push({name: 'connectors'})
            })
            .catch(e => {
                this.errors.push(e)
            })
        },
        backToConnectorsManagament (){
            this.$router.push({name: 'connectors'})
        }
    }
}
</script>

<style scoped>
.error-message{
    font-weight: bold;
    color: red; 
}
</style>


