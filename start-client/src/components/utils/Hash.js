import queryString from 'query-string'
import { toast } from 'react-toastify'
import { useContext, useEffect, useState } from 'react'

import { AppContext } from '../reducer/App'
import { InitializrContext } from '../reducer/Initializr'
import { isValidParams } from './ApiUtils'

const getHash = () => {
  return window.location.hash
}

const clearHash = () => {
  if (window.location.hash) {
    if (window.history.pushState) {
      window.history.pushState(null, null, window.location.pathname)
    } else {
      window.history.hash = ``
    }
  }
}

export default function useHash() {
  const [hash, setHash] = useState(getHash())

  const { dispatch } = useContext(InitializrContext)
  const { config, complete } = useContext(AppContext)

  useEffect(() => {
    const handler = () => {
      setHash(getHash())
    }
    window.addEventListener('hashchange', handler)
    return () => {
      window.removeEventListener('hashchange', handler)
    }
  }, [])

  useEffect(() => {
    if (complete && hash) {
      const params = queryString.parse(`?${hash.substr(2)}`)
      dispatch({ type: 'LOAD', payload: { params, lists: config.lists } })
      clearHash()
      setHash('')
      if (isValidParams(params)) {
        const code = params['?errorcode'] || params.errorcode || null;
        if (code === null) {
          toast.success(`Configuration loaded.`);
        } else if (code === '200' || code === '0') {
          toast.success(params.msg);
        } else {
          toast.error(params.msg || 'success', { autoClose: 10000 });
        }
      }
    }
  }, [complete, hash, dispatch, config])

  return null
}
