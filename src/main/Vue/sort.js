export default class {
    constructor(field, direction) {
        this.field = field;
        this.direction = direction;
    }

    toString() {
        return this.field + "," + this.direction;
    }

    setField(field) {
        if (this.field === field) {
            this.direction = this.direction === 'desc' ? 'asc' : 'desc';
        } else {
            this.field = field;
            this.direction = 'desc';
        }
    }
}