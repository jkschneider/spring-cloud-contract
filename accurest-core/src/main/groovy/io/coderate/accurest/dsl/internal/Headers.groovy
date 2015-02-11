package io.coderate.accurest.dsl.internal

class Headers {

    private Map<String, WithValuePattern> headers = [:]

    WithValuePattern header(String headerName) {
        WithValuePattern withValuePattern = new WithValuePattern()
        headers[headerName] = withValuePattern
        return withValuePattern
    }

    Set<Map.Entry<String, WithValuePattern>> entries() {
        return Collections.unmodifiableSet(headers.entrySet())
    }
}
