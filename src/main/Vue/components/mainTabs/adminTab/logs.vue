<template>
    <table class="table table-bordered table-striped table-sm">
        <thead>
        <tr>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('user')">User</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('act')">Act</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('object')">Object</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('objName')">ObjName</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('date')">Date</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('comment')">Comment</button>
            </th>
        </tr>
        </thead>
        <tbody>
        <tr v-if="logData.content && logData.content.length === 0">
            <td colspan="15">{{ language.data.sf3 }}</td>
        </tr>
        <tr v-for="logRecord in logData.content" :key="'LD' + logRecord.id">
            <td><span v-html="markText(logRecord.user, searchText)"></span></td>
            <td><span v-html="markText(logRecord.act, searchText)"></span></td>
            <td><span v-html="markText(logRecord.object, searchText)"></span></td>
            <td><span v-html="markText(logRecord.objName, searchText)"></span></td>
            <td><span v-html="markText(logRecord.dateStr, searchText)"></span></td>
            <td><span v-html="markText(logRecord.comment, searchText)"></span></td>
        </tr>
        </tbody>
    </table>
    <div class="row justify-content-center">
        <div class="col-auto">
            <select v-model="pager.pageSize" @change="setPageSize">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50" selected>50</option>
                <option value="100">100</option>
            </select>
        </div>
        <div class="col-auto">
            <ul class="pagination pagination-sm">
                <li class="page-item text-dark" v-for="n in logData.totalPages" :class="{active: n - 1 === logData.number}"
                    :key="'LGPager' + n" style="cursor: pointer">
                    <a class="page-link" @click="setPage(n - 1)">{{ n }}</a>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import Pager from "../../../pager";

export default {
    name: "logs",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            pager: new Pager(50, "date", "desc"),
            logData: {},
            searchText: ""
        }
    },
    watch: {
        "pager.pageSize"(newSize, oldSize) {
            if (newSize !== oldSize) {
                this.pager.page = 0;
            }
        }
    },
    computed: {
        isSearch() {
            return this.searchText.length !== 0;
        }
    },
    methods: {
        async getLogs() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    page: Number.parseInt(this.pager.page),
                    size: Number.parseInt(this.pager.pageSize),
                    direction: this.pager.direction,
                    field: this.pager.sortField
                });
                const answer = await $.ajax({
                    url: "/logs/getLogs",
                    method: "POST",
                    data: encryptedData
                });
                this.logData = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async doSearch() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    page: Number.parseInt(this.pager.page),
                    size: Number.parseInt(this.pager.pageSize),
                    direction: this.pager.direction,
                    field: this.pager.sortField,
                    text: this.searchText
                });
                const answer = await $.ajax({
                    url: "/logs/search",
                    method: "POST",
                    data: encryptedData
                });
                this.logData = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        markText(data, text) {
            if (text && data) {
                const regExp = new RegExp(text, "ig");
                return data.replaceAll(regExp, "<mark>$&</mark>");
            }
            return data;
        },
        setSort(field) {
            this.pager.setSort(field);
            if (this.isSearch) {
                this.doSearch();
            } else {
                this.getLogs();
            }
        },
        setPage(page) {
            this.pager.page = page;
            if (this.isSearch) {
                this.doSearch();
            } else {
                this.getLogs();
            }
        },
        setPageSize() {
            if (this.isSearch) {
                this.doSearch();
            } else {
                this.getLogs();
            }
        },
        searchEvent(text) {
            if (text.length === 0) {
                this.searchText = text;
                this.getLogs();
            } else {
                this.searchText = text;
                this.pager.page = 0;
                this.doSearch();
            }
        }
    },
    beforeMount() {
        this.getLogs();
        this.eventHub.emit("logTab-active", true);
    },
    unmounted() {
        this.eventHub.emit("logTab-active", false);
    },
    created() {
        this.eventHub.on("log-search", this.searchEvent);
    },
    beforeUnmount() {
        this.eventHub.off("log-search", this.searchEvent);
    }
}
</script>

<style scoped>

</style>